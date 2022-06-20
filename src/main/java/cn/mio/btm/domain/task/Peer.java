package cn.mio.btm.domain.task;

import java.net.InetSocketAddress;

public class Peer {

    private final InetSocketAddress address;

    private PeerState state;

    public Peer(InetSocketAddress address) {
        this.address = address;
        this.state = PeerState.READY;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public PeerState getState() {
        return state;
    }

    public void connect() {
        this.state = PeerState.CONNECTING;
    }

    public void close() {
        this.state = PeerState.CLOSED;
    }
}
