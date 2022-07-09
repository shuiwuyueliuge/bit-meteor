package cn.mio.btm.domain.task;

import cn.mio.btm.domain.EventBus;

public class PeerActiveEvent implements EventBus.Event {

    private final Peer peer;

    private final String peerId;

    private final String torrId;

    public PeerActiveEvent(Peer peer, String peerId, String torrId) {
        this.peer = peer;
        this.peerId = peerId;
        this.torrId = torrId;
    }

    public Peer getPeer() {
        return peer;
    }

    public String getPeerId() {
        return peerId;
    }

    public String getTorrId() {
        return torrId;
    }
}
