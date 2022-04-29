package cn.mayu.core;

import cn.mayu.protocol.TrackerClient;
import cn.mayu.protocol.TrackerHttpRequest;
import cn.mayu.protocol.TrackerHttpResponse;
import cn.mayu.torrent.TorrentDescriptor;
import cn.mayu.util.FileUtil;
import cn.mayu.util.StringUtil;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

/**
 *
 */
public class TaskManager {

    private final Map<String, Task> taskCache;

    private final ExecutorService trackerService;

    private final ExecutorService peerService;

    private final TrackerClient trackerClient;

    public TaskManager(TrackerClient trackerClient) {
        this.trackerClient = trackerClient;
        this.taskCache = new ConcurrentHashMap<>();
        int worker = Runtime.getRuntime().availableProcessors() * 2;
        trackerService = Executors.newFixedThreadPool(worker, r -> {
            Thread t = new Thread(r);
//            t.setName("trackerThread-");
            return t;
        });
        peerService = Executors.newFixedThreadPool(worker, r -> {
            Thread t = new Thread(r);
//            t.setName("peerThread-");
            return t;
        });
    }

    public Task initTask(String filePath) throws IOException {
        byte[] fileData = FileUtil.readFromFile(filePath);
        if (fileData.length <= 0) {
            throw new IllegalArgumentException(filePath + " can read length is zero");
        }

        String peerId = "-qB1000-" + StringUtil.getRandomString(12);
        Task task = new Task(peerId, TorrentDescriptor.builder().build(fileData));
        taskCache.put(peerId, task);
        trackerService.execute(() -> visitTracker(task));
        //peerService.execute(() -> visitPeer(task));
        return task;
    }

    private void visitPeer(Task task) {
        Iterator<Task.InetSocketAddressWrapper> iterator = task.getInetSocketAddressList().iterator();
        while (iterator.hasNext()) {
            Task.InetSocketAddressWrapper isa = iterator.next();
            if (isa.getStatus() != 0) {
                continue;
            }
            System.out.println(task);
            isa.setStatus(1);
            System.out.println(task);
            Socket socket = new Socket();
            try {
                socket.connect(isa.getAddress(), 3000);
                InputStream inn = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                byte pstrlen = 19;
                String pstr = "BitTorrent protocol";
                byte[] reserved = new byte[8];
                byte[] infoHash = task.getTorrentDescriptor().getInfoHash();
                String peerId = task.getPeerId();
                byte[] data = new byte[1 + pstr.length() + reserved.length + infoHash.length + peerId.length()];
                data[0] = pstrlen;
                System.arraycopy(pstr.getBytes(), 0, data, 1, pstr.length());
                System.arraycopy(reserved, 0, data, 1 + pstr.length(), reserved.length);
                System.arraycopy(infoHash, 0, data, 1 + pstr.length() + reserved.length, infoHash.length);
                System.arraycopy(peerId.getBytes(), 0, data, 1 + pstr.length() + reserved.length + infoHash.length, peerId.length());
                out.write(data);
                byte[] btt = new byte[68];
                int lenn = inn.read(btt);
                if (lenn != 68) {
                    return;
                }

                byte[] bbbb = FileUtil.readFromInput(inn);
                System.out.println(new String(new Hex().encode(bbbb)));
            } catch (Exception e) {
                isa.setStatus(3);
                System.out.println(task);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        peerService.execute(() -> visitPeer(task));
    }

    private void visitTracker(Task task) {
        Selector selector = null;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        task.setStatus("running");
        TorrentDescriptor torrentDescriptor = task.getTorrentDescriptor();
        List<String> announces = torrentDescriptor.getAnnounceList();
        announces.add(torrentDescriptor.getAnnounce());
        Selector finalSelector = selector;
        announces.forEach(announce -> {
            if (!announce.startsWith("http")) {
                return;
            }

            getPeers(
                    announce,
                    torrentDescriptor.getInfoHash(),
                    task.getPeerId(),
                    task.getDownloaded(),
                    torrentDescriptor.getInfo().getFiles().get(0).getLength()
            ).stream()
                    .map(Task.InetSocketAddressWrapper::new)
                    .filter(isa -> !task.getInetSocketAddressList().contains(isa))
//                    .forEach(isa -> task.getInetSocketAddressList().add(isa));
                    .forEach(isa -> {

                        try {
                            SocketChannel socketChannel = SocketChannel.open();
                            socketChannel.configureBlocking(false);
                            socketChannel.connect(isa.getAddress());
                            socketChannel.register(finalSelector, SelectionKey.OP_CONNECT);
                            while (true) {
                                int selectInt = finalSelector.select();
                                if (selectInt == 0) {
                                    continue;
                                }

                                System.out.println(isa.getAddress());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        });

        trackerService.execute(() -> visitTracker(task));
    }

    private List<InetSocketAddress> getPeers(String announce, byte[] infoHash, String peerId, long downloaded, long left) {
        String host = announce.replace("\r", "");
        TrackerHttpRequest request = new TrackerHttpRequest();
        request.setHost(host);
        request.setInfoHash(infoHash);
        request.setPeerId(peerId);
        request.setPort(6881);
        request.setUploaded(0L);
        request.setDownloaded(downloaded);
        request.setLeft(left);
        request.setEvent("started");
        request.setKey(StringUtil.getRandomString(4));
        int connTimeout = 3000;
        int readTimeout = 7000;
        List<InetSocketAddress> isaList = null;
        try {
            TrackerHttpResponse res = trackerClient.connectTracker(request, connTimeout, readTimeout);
            isaList = res.getPeers();
            request.setEvent("stopped");
            trackerClient.connectTracker(request, connTimeout, readTimeout);
        } catch (IOException e) {
            request.setCompact(0);
            request.setNoPeerId(0);
            try {
                TrackerHttpResponse res = trackerClient.connectTracker(request, connTimeout, readTimeout);
                isaList = res.getPeers();
                request.setEvent("stopped");
                trackerClient.connectTracker(request, connTimeout, readTimeout);
            } catch (IOException ioException) {
                // System.out.println(host + " ==> " + e.getMessage());
            }
        }
        System.out.println(isaList);
        return isaList == null ? new ArrayList<>() : isaList;
    }
}
