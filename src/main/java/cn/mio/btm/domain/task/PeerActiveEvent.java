package cn.mio.btm.domain.task;

import cn.mio.btm.domain.EventBus;
import java.nio.channels.Channel;

public class PeerActiveEvent implements EventBus.Event {

    private final Channel channel;

    private final String peerId;

    private final String torrId;

    public PeerActiveEvent(Channel channel, String peerId, String torrId) {
        this.channel = channel;
        this.peerId = peerId;
        this.torrId = torrId;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getPeerId() {
        return peerId;
    }

    public String getTorrId() {
        return torrId;
    }
}
