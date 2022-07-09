package cn.mio.btm.application;

import cn.mio.btm.domain.EventBus;
import cn.mio.btm.domain.task.*;
import cn.mio.btm.domain.torrent.TorrentDescriptorRepository;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 处理查找peers事件。
 */
public class PeerLookupEventConsumer implements EventBus.EventConsumer {

    private final ThreadPoolExecutor peerExecutor;

    private final PeerService peerService;

    private final TorrentDescriptorRepository torrentRepository;

    private final TaskRepository taskRepository;

    public PeerLookupEventConsumer(
            PeerService peerService,
            TorrentDescriptorRepository torrentRepository,
            TaskRepository taskRepository
    ) {
        this.torrentRepository = torrentRepository;
        this.taskRepository = taskRepository;
        int worker = Runtime.getRuntime().availableProcessors() * 2;
        this.peerService = peerService;
        this.peerExecutor = new ThreadPoolExecutor(
                worker,
                worker,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new PeerThreadFactory());
    }

    @Override
    public void receive(EventBus.Event event) {
        PeerLookupEvent runningEvent = (PeerLookupEvent) event;
        taskRepository.findById(runningEvent.getPeerId())
                .ifPresent(task -> {
                    task.setStatus(1);
                    taskRepository.save(task);
                    run(task);
                });

    }

    private void run(Task task) {
        String torrId = task.getTorrentId();
        torrentRepository.findById(torrId)
                .ifPresent(torr -> peerExecutor.execute(() -> {
                    if (task.getStatus() > 1) {
                        run(task);
                        return;
                    }

                    Collection<Peer> peers = peerService.getPeers(task, torr);
                    Collection<Peer> readyPeers = task.filter(peers);
                    taskRepository.save(task);
                    task.publishPeerBeFoundEvent(readyPeers, torr.getInfoHash());
                    run(task);
                }));
    }

    @Override
    public boolean match(Class<?> clazz) {
        return clazz == PeerLookupEvent.class;
    }

    private static class PeerThreadFactory implements ThreadFactory {

        private final AtomicInteger count = new AtomicInteger(0);

        private static final String PREFIX = "peer-finder-thread-";

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            String name = PREFIX + count.getAndIncrement();
            t.setName(name);
            return t;
        }
    }
}
