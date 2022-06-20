package cn.mio.btm.domain.task;

import cn.mio.btm.domain.EventBus;
import java.net.InetSocketAddress;
import java.util.Collection;

public class PeerBeFoundEvent implements EventBus.Event {

    private final Collection<InetSocketAddress> addresses;

    private final String peerId;

    private final byte[] infoHash;

    public PeerBeFoundEvent(Collection<InetSocketAddress> addresses, String peerId, byte[] infoHash) {
        this.addresses = addresses;
        this.peerId = peerId;
        this.infoHash = infoHash;
    }

    public Collection<InetSocketAddress> getAddresses() {
        return addresses;
    }

    public String getPeerId() {
        return peerId;
    }

    public byte[] getInfoHash() {
        return infoHash;
    }
}
