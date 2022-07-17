package cn.mio.btm.infrastructure.peer;

import cn.mio.btm.domain.task.Peer;
import cn.mio.btm.domain.torrent.TorrentDescriptor;
import cn.mio.btm.infrastructure.protocol.PeerHandshakeRequest;
import cn.mio.btm.infrastructure.protocol.PeerHandshakeResponse;
import cn.mio.btm.infrastructure.util.SegmentLock;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

public class NioPeerClient implements PeerClient {

    private static final Map<String, Selector> CONN_SELECTOR_HOLDER = new ConcurrentHashMap<>();

    private static final Map<String, Selector> P2P_SELECTOR_HOLDER = new ConcurrentHashMap<>();

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);

    private final Peer peer;

    private final Map<String, PeerConnection> connections;

    private final Map<PeerProtocolTypeEnum, List<PeerResFuture>> futures;

    private String peerId;

    NioPeerClient(Peer peer) {
        this.peer = peer;
        this.futures = new ConcurrentHashMap<>();
        this.connections = new ConcurrentHashMap<>();
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

        this.peerId = peerId;
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
                        connections.put(peerId, connection);
                        future.complete(connection);
                    }
                }

                keyIterator.remove();
            }

            doConnect(selector, peerId, infoHash, future);
        });
    }

    private void lookupResponse(PeerResFuture future) {

    }

    @Override
    public PeerResFuture bitfield(TorrentDescriptor torr) throws IOException {
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

        int recordBucket = torr.getInfo().getPieceSize() / 8;
        if ((torr.getInfo().getPieceSize() % 8) != 0) {
            recordBucket++;
        }

        byte[] arr = new byte[recordBucket];
        byte[] req = new byte[5 + arr.length];
        byte[] b = integerToBytes(arr.length + 1);
        System.arraycopy(b, 0, req, 0, b.length);
        req[4] = PeerProtocolTypeEnum.BIT_FIELD.getType();
        System.arraycopy(arr, 0, req, 5, arr.length);
        channel.write(ByteBuffer.wrap(req));
        PeerResFuture future = new PeerResFuture();
        List<PeerResFuture> futureList = futures.computeIfAbsent(PeerProtocolTypeEnum.BIT_FIELD, k -> new CopyOnWriteArrayList<>());
        futureList.add(future);
        lookupResponse(future);
        return future;
    }

    private static byte[] integerToBytes(int data) {
        byte[] res = new byte[4];
        res[0] = (byte) (data);
        res[1] = (byte) (data >> 8);
        res[2] = (byte) (data >> 16);
        res[3] = (byte) (data >> 24);
        return res;
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
