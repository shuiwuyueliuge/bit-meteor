package cn.mayu.core;

import cn.mayu.torrent.TorrentDescriptor;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * bt下载任务
 */
public class Task {

    private final TorrentDescriptor torrentDescriptor;

    private final String peerId;

//    private final Set<InetSocketAddress> disConnected;
//
//    private final Set<InetSocketAddress> connecting;
//
//    private final Set<InetSocketAddress> connected;
//
//    private final Set<InetSocketAddress> waitConnect;

    private final List<InetSocketAddressWrapper> inetSocketAddressList;

    private String status;

    private long downloaded;

    public Task(String peerId, TorrentDescriptor torrentDescriptor) {
        this.torrentDescriptor = torrentDescriptor;
        this.peerId = peerId;
        this.downloaded = 0L;
//        disConnected = Collections.synchronizedSet(new HashSet<>());
//        connecting = Collections.synchronizedSet(new HashSet<>());
//        connected = Collections.synchronizedSet(new HashSet<>());
//        waitConnect = Collections.synchronizedSet(new HashSet<>());
        inetSocketAddressList = Collections.synchronizedList(new LinkedList<>());
    }

    public String getPeerId() {
        return peerId;
    }

//    public Set<InetSocketAddress> getWaitConnect() {
//        return waitConnect;
//    }

    public List<InetSocketAddressWrapper> getInetSocketAddressList() {
        return inetSocketAddressList;
    }

    public TorrentDescriptor getTorrentDescriptor() {
        return torrentDescriptor;
    }

//    public Set<InetSocketAddress> getDisConnected() {
//        return disConnected;
//    }
//
//    public Set<InetSocketAddress> getConnecting() {
//        return connecting;
//    }
//
//    public Set<InetSocketAddress> getConnected() {
//        return connected;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    @Override
    public String toString() {
        return "Task{" +
                "torrentDescriptor=" + torrentDescriptor.getInfo().getName() +
                ", peerId='" + peerId + '\'' +
                ", inetSocketAddressList=" + inetSocketAddressList +
//                ", connecting=" + connecting +
//                ", connected=" + connected +
//                ", waitConnect=" + waitConnect +
                ", status='" + status + '\'' +
                ", downloaded=" + downloaded +
                '}';
    }

    public static class InetSocketAddressWrapper {

        private final InetSocketAddress address;

        // 0 waitConnect
        // 1 connecting
        // 2 connected
        // 3 disConnected
        private int status;

        public InetSocketAddressWrapper(InetSocketAddress address) {
            this.address = address;
            this.status = 0;
        }

        public InetSocketAddress getAddress() {
            return address;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InetSocketAddressWrapper that = (InetSocketAddressWrapper) o;
            return Objects.equals(address, that.address);
        }

        @Override
        public int hashCode() {
            return Objects.hash(address);
        }
    }
}
