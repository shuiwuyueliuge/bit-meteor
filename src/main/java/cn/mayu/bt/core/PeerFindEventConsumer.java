package cn.mayu.bt.core;

import cn.mayu.bt.log.LogFactory;
import cn.mayu.bt.log.Logger;
import cn.mayu.bt.protocol.PeerHandshakeRequest;
import org.apache.commons.codec.binary.Hex;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 处理获取到peers事件，发起对peer握手
 */
public class PeerFindEventConsumer implements EventBus.EventConsumer {

    private static final Logger LOG = LogFactory.getLogger(PeerFindEventConsumer.class);

    private final Map<String, Selector> map = new ConcurrentHashMap<>();

    private final List<InetSocketAddress> isaCache = new CopyOnWriteArrayList<>();

    private final int worker = Runtime.getRuntime().availableProcessors() * 2;

    private final ThreadPoolExecutor peerExecutor = new ThreadPoolExecutor(
            worker,
            worker,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new PeerThreadFactory());

    @Override
    public void receive(EventBus.Event event) {
        PeerFindEvent peerFindEvent = (PeerFindEvent) event;
        String peerId = peerFindEvent.getPeerId();
        byte[] infoHash = peerFindEvent.getInfoHash();
        Selector selector = map.computeIfAbsent(peerId, k -> {
            try {
                Selector newSelector = Selector.open();
                connectPeer(newSelector, peerId, infoHash);
                return newSelector;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });

        for (InetSocketAddress isa : peerFindEvent.getPeers()) {
            if (isaCache.contains(isa)) {
                continue;
            }

            try {
                SocketChannel channel = SocketChannel.open();
                channel.configureBlocking(false);
                channel.connect(isa);
                channel.register(selector, SelectionKey.OP_CONNECT);
            } catch (Exception e) {
                continue;
            }

            isaCache.add(isa);
            LOG.info("[Peer Connect] peerId: " + peerFindEvent.getPeerId() + " isa: " + isa);
        }
    }

    private void connectPeer(Selector selector, String peerId, byte[] infoHash) {
        peerExecutor.execute(() -> {
            int keys = 0;
            try {
                keys = selector.select(1000);
            } catch (IOException e) {
                connectPeer(selector, peerId, infoHash);
                return;
            }

            if (keys == 0) {
                connectPeer(selector, peerId, infoHash);
                return;
            }

            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                try {
                    SelectionKey sk = keyIterator.next();
                    if (sk.isConnectable()) {
                        SocketChannel channel = (SocketChannel) sk.channel();
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                        }

                        LOG.info("[Peer Connect] connect success: " + sk);
                        byte[] handshakeData = new PeerHandshakeRequest(peerId, infoHash).getHandshakeRequest();
                        LOG.info("[Peer Connect] write handshake data: " + Hex.encodeHexString(handshakeData) + " to " + sk);
                        channel.write(ByteBuffer.wrap(handshakeData));
                        channel.register(selector, SelectionKey.OP_READ);
                    }

                    if (sk.isReadable()) {
                        LOG.info("[Peer Connect] can read: " + sk);
                        SocketChannel channel = (SocketChannel) sk.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(68);
                        channel.read(byteBuffer);
                        byteBuffer.flip();
                        byte[] b = byteBuffer.array();
                        byte pstrlen = b[0];
                        String pstr = new String(b, 1, 19);
                        byte[] reserved = new byte[8];
                        System.arraycopy(b, 20, reserved, 0, reserved.length);
                        byte[] infoHash1 = new byte[20];
                        System.arraycopy(b, 28, infoHash1, 0, infoHash1.length);
                        String peerId1 = new String(b, 48, 20);
                        System.out.println(peerId1);
                    }

                    keyIterator.remove();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    keyIterator.remove();
                }
            }

            connectPeer(selector, peerId, infoHash);
        });
    }

    @Override
    public boolean match(Class<?> clazz) {
        return clazz == PeerFindEvent.class;
    }

    private static class PeerThreadFactory implements ThreadFactory {

        private final AtomicInteger count = new AtomicInteger(0);

        private static final String PREFIX = "peer-connect-thread-";

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            String name = PREFIX + count.getAndIncrement();
            t.setName(name);
            return t;
        }
    }
}
