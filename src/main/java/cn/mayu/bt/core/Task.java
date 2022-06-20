//package cn.mayu.bt.core;
//
//import cn.mayu.bt.torrent.TorrentDescriptor;
//import java.net.InetSocketAddress;
//import java.util.*;
//import java.util.concurrent.CopyOnWriteArrayList;
//
///**
// * bt下载任务
// */
//public class Task {
//
//    private final TorrentDescriptor torrentDescriptor;
//
//    private final String peerId;
//
//    private final List<InetSocketAddressWrapper> inetSocketAddressList;
//
//    // 0 等待 1 运行 2 完成 3 异常停止
//    private int status;
//
//    private long downloaded;
//
//    public Task(String peerId, TorrentDescriptor torrentDescriptor) {
//        this.torrentDescriptor = torrentDescriptor;
//        this.peerId = peerId;
//        this.downloaded = 0L;
//        this.status = 0;
//        inetSocketAddressList = new CopyOnWriteArrayList<>();
//    }
//
//    public String getPeerId() {
//        return peerId;
//    }
//
//    public List<InetSocketAddressWrapper> getInetSocketAddressList() {
//        return inetSocketAddressList;
//    }
//
//    public TorrentDescriptor getTorrentDescriptor() {
//        return torrentDescriptor;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public long getDownloaded() {
//        return downloaded;
//    }
//
//    public void setDownloaded(long downloaded) {
//        this.downloaded = downloaded;
//    }
//
//    @Override
//    public String toString() {
//        return "Task{" +
//                "torrentDescriptor=" + torrentDescriptor.getInfo().getName() +
//                ", peerId='" + peerId +
//                ", inetSocketAddressList=" + inetSocketAddressList +
//                ", status='" + status +
//                ", downloaded=" + downloaded +
//                '}';
//    }
//
//    public static class InetSocketAddressWrapper {
//
//        private final InetSocketAddress address;
//
//        // 0 waitConnect
//        // 1 connecting
//        // 2 connected
//        // 3 disConnected
//        private int status;
//
//        public InetSocketAddressWrapper(InetSocketAddress address) {
//            this.address = address;
//            this.status = 0;
//        }
//
//        public InetSocketAddress getAddress() {
//            return address;
//        }
//
//        public int getStatus() {
//            return status;
//        }
//
//        public void setStatus(int status) {
//            this.status = status;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            InetSocketAddressWrapper that = (InetSocketAddressWrapper) o;
//            return Objects.equals(address, that.address);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(address);
//        }
//    }
//}
