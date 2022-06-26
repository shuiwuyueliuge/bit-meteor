package cn.mio.btm.domain.task;

import cn.mio.btm.domain.EventBus;

import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * bt下载任务
 */
public class Task {

    private final String torrentId;

    private final String peerId;

    // 0 等待 1 运行 2 完成 3 异常停止
    private int status;

    private long downloaded;

    private final Set<Peer> peers;

    public Task(String peerId, String torrentId) {
        this.torrentId = torrentId;
        this.peerId = peerId;
        this.downloaded = 0L;
        this.status = 0;
        peers = new HashSet<>();
    }

    public String getPeerId() {
        return peerId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public String getTorrentId() {
        return torrentId;
    }

    public Set<Peer> getPeers() {
        return peers;
    }

    public Collection<Peer> filter(Collection<Peer> newPeers) {
        Map<InetSocketAddress, Peer> map = peers.stream().collect(Collectors.toMap(p -> p.getAddress().getAddress(), Function.identity(), (a, b) -> a));
        newPeers.forEach(peer -> {
            Peer cached = map.get(peer.getAddress().getAddress());
            if (Objects.nonNull(cached) && cached.getState() == PeerState.READY && !cached.getAddress().canReady()) {
                return;
            }

            peers.add(peer);
        });

        return peers.stream().filter(p -> p.getState() == PeerState.READY).collect(Collectors.toList());
    }

    public void publishPeerLookupEvent() {
        EventBus.publish(new PeerLookupEvent(torrentId, peerId));
    }

    public void publishPeerBeFoundEvent(Collection<Peer> peers, byte[] infoHash) {
        EventBus.publish(new PeerBeFoundEvent(peers.stream().map(p -> p.getAddress().getAddress()).collect(Collectors.toList()), peerId, infoHash));
    }

    public void peerActive(InetSocketAddress activeAddress) {
        Map<InetSocketAddress, Peer> map = peers.stream().collect(Collectors.toMap(p -> p.getAddress().getAddress(), Function.identity(), (a, b) -> a));
        Peer cached = map.get(activeAddress);
        if (Objects.nonNull(cached) && cached.getState() == PeerState.READY) {
            peers.remove(cached);
        }

        peers.add(new Peer(activeAddress, PeerState.ACTIVE));
    }

    public void publishPeerActiveEvent(Channel channel) {
        EventBus.publish(new PeerActiveEvent(channel, peerId, this.torrentId));
    }

    @Override
    public String toString() {
        return "Task{" +
                "torrentId='" + torrentId + '\'' +
                ", peerId='" + peerId + '\'' +
                ", status=" + status +
                ", downloaded=" + downloaded +
                ", peers=" + peers +
                '}';
    }
}
