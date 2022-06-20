//package cn.mayu.bt.core;
//
//import cn.mayu.bt.torrent.TorrentDescriptor;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// *
// */
//public class TaskRunningEventConsumer implements EventBus.EventConsumer {
//
//    private final ThreadPoolExecutor peerExecutor;
//
//    private final PeersFinder peersFinder;
//
//    public TaskRunningEventConsumer(PeersFinder peersFinder) {
//        int worker = Runtime.getRuntime().availableProcessors() * 2;
//        this.peersFinder = peersFinder;
//        this.peerExecutor = new ThreadPoolExecutor(
//                worker,
//                worker,
//                60L,
//                TimeUnit.SECONDS,
//                new LinkedBlockingQueue<>(),
//                new PeerThreadFactory());
//    }
//
//    @Override
//    public void receive(EventBus.Event event) {
//        TaskRunningEvent runningEvent = (TaskRunningEvent) event;
//        Task task = runningEvent.getTask();
//        task.setStatus(1);
//        run(task);
//    }
//
//    private void run(Task task) {
//        TorrentDescriptor torrentDescriptor = task.getTorrentDescriptor();
//        peerExecutor.execute(() -> {
//            if (task.getStatus() > 1) {
//                run(task);
//                return;
//            }
//
//            peersFinder.findPeers(
//                    torrentDescriptor.getAnnounce(),
//                    torrentDescriptor.getInfoHash(),
//                    task.getPeerId(),
//                    task.getDownloaded(),
//                    torrentDescriptor.getInfo().getFiles().get(0).getLength()
//            );
//
//            run(task);
//        });
//    }
//
//    @Override
//    public boolean match(Class<?> clazz) {
//        return clazz == TaskRunningEvent.class;
//    }
//
//    private static class PeerThreadFactory implements ThreadFactory {
//
//        private final AtomicInteger count = new AtomicInteger(0);
//
//        private static final String PREFIX = "peer-finder-thread-";
//
//        @Override
//        public Thread newThread(Runnable r) {
//            Thread t = new Thread(r);
//            String name = PREFIX + count.getAndIncrement();
//            t.setName(name);
//            return t;
//        }
//    }
//
//}
