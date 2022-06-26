package cn.mio.btm.domain.task;

import java.net.InetSocketAddress;
import java.util.Objects;

public class PeerAddress {

    private static final long TIME = 1000 * 60 * 10;

    private final InetSocketAddress address;

    private final long reConnectTime;

    public PeerAddress(InetSocketAddress address) {
        this.address = address;
        this.reConnectTime = System.currentTimeMillis() + TIME;
    }

    public boolean canReady() {
        return reConnectTime <= System.currentTimeMillis();
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeerAddress that = (PeerAddress) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public String toString() {
        return "PeerAddress{" +
                "address=" + address +
                ", reConnectTime=" + reConnectTime +
                '}';
    }
}
