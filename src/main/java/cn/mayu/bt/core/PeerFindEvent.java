package cn.mayu.bt.core;

import org.apache.commons.codec.binary.Hex;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 获取到peers事件
 */
public class PeerFindEvent implements EventBus.Event {

    private final String peerId;

    private final byte[] infoHash;

    private final List<InetSocketAddress> peers;

    public PeerFindEvent(String peerId, byte[] hashInfo, List<InetSocketAddress> peers) {
        this.peerId = peerId;
        this.infoHash = hashInfo;
        this.peers = peers;
    }

    public String getPeerId() {
        return peerId;
    }

    public List<InetSocketAddress> getPeers() {
        return peers;
    }

    public byte[] getInfoHash() {
        return infoHash;
    }

    @Override
    public String toString() {
        return "PeerFindEvent{" +
                "peerId='" + peerId + '\'' +
                ", infoHash=" + Hex.encodeHexString(infoHash) +
                ", peers=" + peers +
                '}';
    }
}
