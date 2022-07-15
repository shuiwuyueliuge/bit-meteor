package cn.mio.btm.infrastructure.peer;

import cn.mio.btm.domain.task.Peer;
import cn.mio.btm.infrastructure.protocol.PeerHandshakeRequest;
import cn.mio.btm.infrastructure.protocol.PeerHandshakeResponse;
import cn.mio.btm.infrastructure.util.SegmentLock;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

public class NioPeerClient implements PeerClient {

    private static final Map<String, Selector> CONN_SELECTOR_HOLDER = new ConcurrentHashMap<>();

//    private static final Map<String, Selector> P2P_SELECTOR_HOLDER = new ConcurrentHashMap<>();

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);

    private final Collection<PeerConnection> connections;

    private final Peer peer;

    NioPeerClient(Peer peer) {
        this.peer = peer;
        this.connections = new CopyOnWriteArrayList<>();
    }

    @Override
    @SuppressWarnings("all")
    public CompletableFuture<PeerConnection> connect(String peerId, byte[] infoHash) throws IOException {
        Selector cached = CONN_SELECTOR_HOLDER.get(peerId);
        if (Objects.isNull(cached)) {
            Lock lock = SegmentLock.getLock(peerId);
            try {
                lock.lock();
                cached = Selector.open();
                CONN_SELECTOR_HOLDER.put(peerId, cached);
            } finally {
                lock.unlock();
            }
        }

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(this.peer.getAddress().getAddress());
        channel.register(cached, SelectionKey.OP_CONNECT);
        CompletableFuture<PeerConnection> future = new CompletableFuture();
        doConnect(cached, peerId, infoHash, future);
        return future;
    }

    private void doConnect(Selector selector, String peerId, byte[] infoHash, CompletableFuture<PeerConnection> future) {
        EXECUTOR.execute(() -> {
            if (future.isCancelled()) {
                return;
            }

            int keys = 0;
            try {
                keys = selector.select(1000);
            } catch (IOException e) {
                doConnect(selector, peerId, infoHash, future);
                return;
            }

            if (keys == 0) {
                doConnect(selector, peerId, infoHash, future);
                return;
            }

            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey sk = keyIterator.next();
                if (sk.isConnectable()) {
                    SocketChannel channel = (SocketChannel) sk.channel();
                    if (channel.isConnectionPending()) {
                        try {
                            channel.finishConnect();
                        } catch (IOException e) {
                            continue;
                        }
                    }

                    try {
                        byte[] handshakeData = new PeerHandshakeRequest(peerId, infoHash).getHandshakeRequest();
                        channel.write(ByteBuffer.wrap(handshakeData));
                        channel.register(selector, SelectionKey.OP_READ);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                        keyIterator.remove();
                        continue;
                    }
                }

                if (sk.isReadable()) {
                    SocketChannel channel = (SocketChannel) sk.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(68);
                    try {
                        channel.read(byteBuffer);
                    } catch (IOException e) {
                        future.completeExceptionally(e);
                        keyIterator.remove();
                        continue;
                    }

                    byteBuffer.flip();
                    byte[] b = byteBuffer.array();
                    PeerHandshakeResponse handshakeResponse = new PeerHandshakeResponse(b);
                    if (handshakeResponse.validate(infoHash)) {
                        PeerConnection connection = new PeerConnectionImpl(channel);
                        connections.add(connection);
                        future.complete(connection);
                    }
                }

                keyIterator.remove();
            }

            doConnect(selector, peerId, infoHash, future);
        });
    }

    @Override
    public PeerResFuture bitfield() {
        return null;
    }

    @Override
    public PeerResFuture request() {
        return null;
    }

    @Override
    public void keepAlive() {

    }

    @Override
    public Peer getPeer() {
        return null;
    }
}
