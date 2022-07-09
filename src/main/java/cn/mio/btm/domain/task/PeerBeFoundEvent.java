package cn.mio.btm.domain.task;

import cn.mio.btm.domain.EventBus;
import java.util.Collection;

public class PeerBeFoundEvent implements EventBus.Event {

    private final Collection<Peer> peers;

    private final String peerId;

    private final byte[] infoHash;

    public PeerBeFoundEvent(Collection<Peer> peers, String peerId, byte[] infoHash) {
        this.peers = peers;
        this.peerId = peerId;
        this.infoHash = infoHash;
    }

    public Collection<Peer> getPeers() {
        return peers;
    }

    public String getPeerId() {
        return peerId;
    }

    public byte[] getInfoHash() {
        return infoHash;
    }
}
