package cn.mio.btm.infrastructure.peer;

import cn.mio.btm.domain.task.Peer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeerClientManager {

    private static final Map<String, PeerClientHolder> CLIENT_HOLDER_MAP = new ConcurrentHashMap<>();

    public static PeerClient getClient(String peerId, Peer peer) {
        PeerClientHolder holder = CLIENT_HOLDER_MAP
                .computeIfAbsent(peerId, k -> new PeerClientHolder());
        return holder.getClient(peer);
    }

    private static class PeerClientHolder {

        private final Map<Peer, PeerClient> clients = new ConcurrentHashMap<>();

        public PeerClient getClient(Peer peer) {
            return clients.computeIfAbsent(peer, k -> new NioPeerClient(peer));
        }
    }
}
