package cn.mio.btm.application;

import cn.mio.btm.domain.EventBus;
import cn.mio.btm.domain.task.Peer;
import cn.mio.btm.domain.task.PeerBeFoundEvent;
import cn.mio.btm.domain.task.TaskRepository;
import cn.mio.btm.infrastructure.log.LogFactory;
import cn.mio.btm.infrastructure.log.Logger;
import cn.mio.btm.infrastructure.peer.PeerClient;
import cn.mio.btm.infrastructure.peer.PeerClientManager;
import java.util.Objects;

/**
 * 对发现的peers进行链接。
 */
public class PeerBeFoundEventConsumer implements EventBus.EventConsumer {

    private static final Logger LOG = LogFactory.getLogger(PeerBeFoundEventConsumer.class);

    private final TaskRepository taskRepository;

    public PeerBeFoundEventConsumer(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void receive(EventBus.Event event) {
        PeerBeFoundEvent beFoundEvent = (PeerBeFoundEvent) event;
        String peerId = beFoundEvent.getPeerId();
        byte[] infoHash = beFoundEvent.getInfoHash();
        try {
            for (Peer peer : beFoundEvent.getPeers()) {
                PeerClient client = PeerClientManager.getClient(peerId, peer);
                client.connect(peerId, infoHash)
                        .whenComplete((connection, error) -> {
                            if (Objects.nonNull(error)) {
                                return;
                            }

                            LOG.info("peer handshake success and remote: " + peer.getAddress().getAddress() + " peerId: " + peerId);
                            taskRepository.findById(peerId)
                                    .ifPresent(task -> {
                                        task.peerActive(peer.getAddress().getAddress());
                                        taskRepository.save(task);
                                        task.publishPeerActiveEvent(peer);
                                    });
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean match(Class<?> clazz) {
        return clazz == PeerBeFoundEvent.class;
    }
}
