package cn.mio.btm.domain.task;

import cn.mio.btm.domain.EventBus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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

    public void addPeers(Collection<Peer> peers) {
        this.peers.addAll(peers);
    }

    public void publishPeerLookupEvent() {
        EventBus.publish(new PeerLookupEvent(torrentId, peerId));
    }

    public void publishPeerBeFoundEvent(Collection<Peer> peers, byte[] infoHash) {
        EventBus.publish(new PeerBeFoundEvent(peers.stream().map(Peer::getAddress).collect(Collectors.toList()), peerId, infoHash));
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
