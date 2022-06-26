package cn.mio.btm.domain.task;

import java.net.InetSocketAddress;
import java.util.Objects;

public class Peer {

    private final PeerAddress peerAddress;

    private PeerState state;

    public Peer(InetSocketAddress address, PeerState state) {
        this.peerAddress = new PeerAddress(address);
        this.state = state;
    }

    public PeerAddress getAddress() {
        return peerAddress;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return Objects.equals(peerAddress, peer.peerAddress) && state == peer.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(peerAddress, state);
    }

    @Override
    public String toString() {
        return "Peer{" +
                "peerAddress=" + peerAddress +
                ", state=" + state +
                '}';
    }
}
