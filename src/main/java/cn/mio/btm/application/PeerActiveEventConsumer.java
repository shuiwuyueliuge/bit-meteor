//package cn.mio.btm.application;
//
//import cn.mio.btm.domain.EventBus;
//import cn.mio.btm.domain.task.Peer;
//import cn.mio.btm.domain.task.PeerActiveEvent;
//import cn.mio.btm.domain.task.TaskRepository;
//import cn.mio.btm.domain.torrent.TorrentDescriptorRepository;
//import cn.mio.btm.infrastructure.log.LogFactory;
//import cn.mio.btm.infrastructure.log.Logger;
//import cn.mio.btm.infrastructure.peer.PeerClient;
//import cn.mio.btm.infrastructure.peer.PeerClientManager;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.SocketChannel;
//import java.util.Iterator;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * 对建立连接的peers传输数据。
// */
//public class PeerActiveEventConsumer implements EventBus.EventConsumer {
//
//    private static final Logger LOG = LogFactory.getLogger(PeerActiveEventConsumer.class);
//
//    private final TaskRepository taskRepository;
//
//    private final TorrentDescriptorRepository torrentRepository;
//
//    public PeerActiveEventConsumer(TaskRepository taskRepository, TorrentDescriptorRepository torrentRepository) {
//        this.taskRepository = taskRepository;
//        this.torrentRepository = torrentRepository;
//    }
//
//    @Override
//    public void receive(EventBus.Event event) {
//        PeerActiveEvent activeEvent = (PeerActiveEvent) event;
//        String peerId = activeEvent.getPeerId();
//        Peer peer = activeEvent.getPeer();
//        PeerClient client = PeerClientManager.getClient(peerId, peer);
//        client.bitfield();
//
//
////        PeerActiveEvent activeEvent = (PeerActiveEvent) event;
////        SocketChannel channel = (SocketChannel) activeEvent.getChannel();
////        Selector selector = map.computeIfAbsent(activeEvent.getPeerId(), k -> {
////            try {
////                Selector newSelector = Selector.open();
////                activePeer(newSelector);
////                return newSelector;
////            } catch (IOException e) {
////                throw new IllegalStateException(e);
////            }
////        });
////
////        torrentRepository.findById(activeEvent.getTorrId())
////                .ifPresent(torr -> {
////                    try {
////                        channel.register(selector, SelectionKey.OP_READ);
////                        int recordBucket = torr.getInfo().getPieceSize() / 8;
////                        if ((torr.getInfo().getPieceSize() % 8) != 0) {
////                            recordBucket++;
////                        }
////
////                        byte[] arr = new byte[recordBucket];
////                        byte[] req = new byte[5 + arr.length];
////                        byte[] b = integerToBytes(arr.length + 1);
////                        System.arraycopy(b, 0, req, 0, b.length);
////                        req[4] = (byte) 5;
////                        System.arraycopy(arr, 0, req, 5, arr.length);
////                        channel.write(ByteBuffer.wrap(req));
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                });
////        try {
////            LOG.debug("[Peer Active] peerId: " + activeEvent.getPeerId() + " address: " + channel.getRemoteAddress());
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }
//
//    private static byte[] integerToBytes(int data) {
//        byte[] res = new byte[4];
//        res[0] = (byte) (data);
//        res[1] = (byte) (data >> 8);
//        res[2] = (byte) (data >> 16);
//        res[3] = (byte) (data >> 24);
//        return res;
//    }
//
//    private static int bytesToInteger(byte[] b) {
//        int i = 0;
//        i += ((b[0] & 0xFF) << 24);
//        i += ((b[1] & 0xFF) << 16);
//        i += ((b[2] & 0xFF) << 8);
//        i += (b[3] & 0xFF);
//        return i;
//    }
//
//    private void activePeer(Selector selector) {
//        peerExecutor.execute(() -> {
//            int keys;
//            try {
//                keys = selector.select(1000);
//            } catch (IOException e) {
//                activePeer(selector);
//                return;
//            }
//
//            if (keys == 0) {
//                activePeer(selector);
//                return;
//            }
//
//            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
//            while (keyIterator.hasNext()) {
//                try {
//                    SelectionKey sk = keyIterator.next();
//                    if (sk.isReadable()) {
//                        LOG.debug("[Peer Connect] can read: " + sk);
//                        SocketChannel channel = (SocketChannel) sk.channel();
//                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//                        channel.read(byteBuffer);
//                        byteBuffer.flip();
//                        byte[] b = byteBuffer.array();
//                        byte[] len = new byte[4];
//                        System.arraycopy(b, 0, len, 0, 4);
//                        int length = bytesToInteger(len);
//                        if (length != 0) {
//                            map.entrySet().stream().filter(entry -> entry.getValue().equals(selector))
//                                    .findFirst()
//                                    .ifPresent(entry -> {
//                                        String peerId = entry.getKey();
//                                        taskRepository.findById(peerId)
//                                                .flatMap(task -> torrentRepository.findById(task.getTorrentId()))
//                                                .ifPresent(torr -> {
//                                                    int recordBucket = torr.getInfo().getPieceSize() / 8;
//                                                    if ((torr.getInfo().getPieceSize() % 8) != 0) {
//                                                        recordBucket++;
//                                                    }
//
//                                                    int expectLen = recordBucket + 1;
//                                                    if (expectLen == length) {
//                                                        byte command = byteBuffer.get(4);
//                                                        if (command != (byte) 5) {
//                                                            return;
//                                                        }
//
//                                                        byte[] data = new byte[length - 1];
//                                                        byteBuffer.position(5);
//                                                        byteBuffer.get(data, 0, data.length);
//                                                        // &
//                                                        // 128 64 32 16 8 4 2 1
//                                                        //   7  6  5  4 3 2 1 0
//                                                        //   0 代表当前分片没有下载
//                                                        for(int i = 0; i < recordBucket; i++) {
//                                                            System.out.println(i + " piece");
//                                                            System.out.println(data[i] & 128);
//                                                            System.out.println(data[i] & 64);
//                                                            System.out.println(data[i] & 32);
//                                                            System.out.println(data[i] & 16);
//                                                            System.out.println(data[i] & 8);
//                                                            System.out.println(data[i] & 4);
//                                                            System.out.println(data[i] & 2);
//                                                            System.out.println(data[i] & 1);
//                                                        }
//                                                    }
//                                                });
//                                    });
//                        }
//                    }
//
//                    keyIterator.remove();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    keyIterator.remove();
//                }
//            }
//
//            activePeer(selector);
//        });
//    }
//
//    @Override
//    public boolean match(Class<?> clazz) {
//        return clazz == PeerActiveEvent.class;
//    }
//
//    private static class PeerThreadFactory implements ThreadFactory {
//
//        private final AtomicInteger count = new AtomicInteger(0);
//
//        private static final String PREFIX = "peer-active-thread-";
//
//        @Override
//        public Thread newThread(Runnable r) {
//            Thread t = new Thread(r);
//            String name = PREFIX + count.getAndIncrement();
//            t.setName(name);
//            return t;
//        }
//    }
//}
