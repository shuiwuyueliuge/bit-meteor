package cn.mio.btm.domain.task;

import cn.mio.btm.domain.EventBus;

public class PeerLookupEvent implements EventBus.Event {

    private final String torrId;

    private final String peerId;

    public PeerLookupEvent(String torrId, String peerId) {
        this.torrId = torrId;
        this.peerId = peerId;
    }

    public String getTorrId() {
        return torrId;
    }

    public String getPeerId() {
        return peerId;
    }
}
