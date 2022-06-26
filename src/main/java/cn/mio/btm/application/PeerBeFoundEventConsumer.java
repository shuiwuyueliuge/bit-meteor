package cn.mio.btm.application;

import cn.mio.btm.domain.EventBus;
import cn.mio.btm.domain.task.PeerBeFoundEvent;
import cn.mio.btm.domain.task.TaskRepository;
import cn.mio.btm.infrastructure.log.LogFactory;
import cn.mio.btm.infrastructure.log.Logger;
import cn.mio.btm.infrastructure.protocol.PeerHandshakeRequest;
import cn.mio.btm.infrastructure.protocol.PeerHandshakeResponse;
import org.apache.commons.codec.binary.Hex;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对发现的peers进行链接。
 */
public class PeerBeFoundEventConsumer implements EventBus.EventConsumer {

    private static final Logger LOG = LogFactory.getLogger(PeerBeFoundEventConsumer.class);

    private final Map<String, Selector> map = new ConcurrentHashMap<>();

    private final TaskRepository taskRepository;

    private final int worker = Runtime.getRuntime().availableProcessors() * 2;

    private final ThreadPoolExecutor peerExecutor = new ThreadPoolExecutor(
            worker,
            worker,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new PeerBeFoundEventConsumer.PeerThreadFactory());

    public PeerBeFoundEventConsumer(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void receive(EventBus.Event event) {
        PeerBeFoundEvent beFoundEvent = (PeerBeFoundEvent) event;
        String peerId = beFoundEvent.getPeerId();
        byte[] infoHash = beFoundEvent.getInfoHash();
        Selector selector = map.computeIfAbsent(peerId, k -> {
            try {
                Selector newSelector = Selector.open();
                connectPeer(newSelector, peerId, infoHash);
                return newSelector;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });

        for (InetSocketAddress isa : beFoundEvent.getAddresses()) {
            try {
                SocketChannel channel = SocketChannel.open();
                channel.configureBlocking(false);
                channel.connect(isa);
                channel.register(selector, SelectionKey.OP_CONNECT);
            } catch (Exception e) {
                continue;
            }

            LOG.debug("[Peer Connect] peerId: " + peerId + " address: " + isa);
        }
    }

    private void connectPeer(Selector selector, String peerId, byte[] infoHash) {
        peerExecutor.execute(() -> {
            int keys;
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

                        LOG.debug("[Peer Connect] connect success: " + sk);
                        byte[] handshakeData = new PeerHandshakeRequest(peerId, infoHash).getHandshakeRequest();
                        LOG.debug("[Peer Connect] write handshake data: " + Hex.encodeHexString(handshakeData) + " to " + sk);
                        channel.write(ByteBuffer.wrap(handshakeData));
                        channel.register(selector, SelectionKey.OP_READ);
                    }

                    if (sk.isReadable()) {
                        LOG.debug("[Peer Connect] can read: " + sk);
                        SocketChannel channel = (SocketChannel) sk.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(68);
                        channel.read(byteBuffer);
                        byteBuffer.flip();
                        byte[] b = byteBuffer.array();
                        PeerHandshakeResponse handshakeResponse = new PeerHandshakeResponse(b);
                        LOG.debug("peer handshake response: " + handshakeResponse);
                        if (handshakeResponse.validate(infoHash)) {
                            InetSocketAddress remote = (InetSocketAddress) channel.getRemoteAddress();
                            LOG.info("peer handshake success and remote: " + remote + " peerId: " + handshakeResponse.getPeerId());
                            taskRepository.findById(peerId)
                                    .ifPresent(task -> {
                                        task.peerActive(remote);
                                        taskRepository.save(task);
                                        task.publishPeerActiveEvent(channel);
                                    });
//                            taskRepository.findById(peerId)
//                                    .flatMap(task -> torrentRepository.findById(task.getTorrentId()))
//                                    .ifPresent(torr -> {
//                                        BitSet bitSet = new BitSet(torr.getInfo().getPieceSize());
//                                        byte[] arr = bitSet.toByteArray();
//                                        byte[] req = new byte[2 + arr.length];
//                                        req[0] = (byte)(arr.length + 1);
//                                        req[1] = 5;
//                                        System.arraycopy(arr, 0, req, 2, arr.length);
//                                        try {
//                                            channel.write(ByteBuffer.wrap(req));
//                                            channel.register(selector, SelectionKey.OP_READ);
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    });
                        }
                    }

                    keyIterator.remove();
                } catch (Exception e) {
                    keyIterator.remove();
                }
            }

            connectPeer(selector, peerId, infoHash);
        });
    }

    @Override
    public boolean match(Class<?> clazz) {
        return clazz == PeerBeFoundEvent.class;
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
