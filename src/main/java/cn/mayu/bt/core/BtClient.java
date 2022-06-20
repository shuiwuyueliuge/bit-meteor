//package cn.mayu.bt.core;
//
//import cn.mayu.bt.bencoding.BencodingParseException;
//import cn.mayu.bt.protocol.DefaultTrackerClient;
//import cn.mayu.bt.protocol.TrackerHttpRequest;
//import cn.mayu.bt.protocol.TrackerHttpResponse;
//import cn.mayu.bt.torrent.TorrentDescriptor;
//import cn.mio.btm.infrastructure.util.FileUtil;
//import org.apache.commons.codec.binary.Hex;
//import java.io.*;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Queue;
//import java.util.Set;
//import java.util.concurrent.LinkedBlockingDeque;
//
///**
// *
// */
//public class BtClient {
//
//    Queue<InetSocketAddress> q = new LinkedBlockingDeque<>();
//
//    Set<InetSocketAddress> set = new HashSet<>();
//
//    public void start(String filePath) throws IOException {
//        byte[] fileData = FileUtil.readFromFile(filePath);
//        if (fileData.length <= 0) {
//            return;
//        }
//
//        TorrentDescriptor td = null;
//        new Thread(() -> {
//            while (true) {
//                InetSocketAddress a = q.poll();
//                if (a == null) {
//                    continue;
//                }
//
//                Socket socket = new Socket();
//                try {
//                    socket.connect(a, 3000);
//                    InputStream inn = socket.getInputStream();
//                    OutputStream out = socket.getOutputStream();
//                    byte pstrlen = 19;
//                    String pstr = "BitTorrent protocol";
//                    byte[] reserved = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
//                    byte[] infoHash = td.getInfoHash();
//                    String peerId = "12345678901234567890";
//                    byte[] data = new byte[1 + pstr.length() + reserved.length + infoHash.length + peerId.length()];
//                    data[0] = pstrlen;
//                    System.arraycopy(pstr.getBytes(), 0, data, 1, pstr.length());
//                    System.arraycopy(reserved, 0, data, 1 + pstr.length(), reserved.length);
//                    System.arraycopy(infoHash, 0, data, 1 + pstr.length() + reserved.length, infoHash.length);
//                    System.arraycopy(peerId.getBytes(), 0, data, 1 + pstr.length() + reserved.length + infoHash.length, peerId.length());
//                    out.write(data);
//                    byte[] btt = new byte[68];
//                    int lenn = inn.read(btt);
//                    if (lenn != 68) {
//                        return;
//                    }
//
//                    byte[] bbbb = FileUtil.readFromInput(inn);
//                    System.out.println(new String(new Hex().encode(bbbb)));
//                } catch (Exception e) {
//                    set.add(a);
////                    System.out.println(a + " " + e.getMessage());
//                } finally {
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//
//        new Thread(() -> {
//            while(true) {
//                connectTracker(td);
//            }
//        }).start();
//    }
//
//
//    private void connectTracker(TorrentDescriptor td) {
//        List<String> announces = td.getAnnounceList();
//        announces.add(td.getAnnounce());
//        announces.forEach(announce -> {
//            if (!announce.startsWith("http")) {
//                return;
//            }
//
//            String host = announce.replace("\r", "");
//            TrackerHttpRequest request = new TrackerHttpRequest();
//            request.setHost(host);
//            request.setInfoHash(td.getInfoHash());
//            request.setPeerId("12345678901234567890");
//            request.setPort(6881);
//            request.setUploaded(0L);
//            request.setDownloaded(0L);
//            request.setLeft(td.getInfo().getFiles().get(0).getLength());
//            request.setEvent("started");
//            request.setKey("4546");
//            try {
//                connect(request);
//                request.setEvent("stopped");
//                connect(request);
//            } catch (IOException | BencodingParseException e) {
//                request.setCompact(0);
//                request.setNoPeerId(0);
//                try {
//                    connect(request);
//                    request.setEvent("stopped");
//                    connect(request);
//                } catch (IOException | BencodingParseException ioException) {
//                   // System.out.println(host + " ==> " + e.getMessage());
//                }
//            }
//        });
//    }
//
//    private void connect(TrackerHttpRequest request) throws IOException, BencodingParseException {
//        TrackerHttpResponse res = new DefaultTrackerClient().connectTracker(request, 3000, 7000);
//        List<InetSocketAddress> list = res.getPeers();
//        if(list == null || list.size() <= 0) {
//            return;
//        }
//
//        list.forEach(a -> {
//            if (set.contains(a) || q.contains(a)) {
//                return;
//            }
//
//            q.add(a);
//        });
//    }
//}
///**
// * 2022-03-15 10:59:04	schedule next announce in 00:20
// * 2022-03-15 10:59:24	Announce START:
// *
//* http://t.nyaatracker.com/announce?
//* info_hash=%E5%072%BF%E6%10%ADp%0D%1C%B3%7C%B5%F7%09%C4~j%3E%9E
//* &peer_id=-BC0186-%B2%5D%1A%1F%A6%FF%AC%8D%7C.5%C3
//* &port=26715
//* &natmapped=1
//* &localip=192.168.0.114
//* &port_type=lan
//* &uploaded=0
//* &downloaded=1277952
//* &left=376078336
//* &numwant=200
//* &compact=1
//* &no_peer_id=1
//* &key=5845
//* &event=started
// *
// * 2022-03-15 10:59:24	Start connecting...
// * 2022-03-15 10:59:29	Tracker returned info: interval = 30:00, min interval = 30:00
// * 2022-03-15 10:59:29	Tracker returned info: complete = 0, incomplete = 1
// * 2022-03-15 10:59:29	schedule next announce in 30:00
// * 2022-03-15 10:59:29	Logged in; Tracker returned 1 peers
// * 2022-03-15 11:00:13	Announce STOP:
// * http://t.nyaatracker.com/announce?
// * info_hash=%E5%072%BF%E6%10%ADp%0D%1C%B3%7C%B5%F7%09%C4~j%3E%9E
// * &peer_id=-BC0186-%B2%5D%1A%1F%A6%FF%AC%8D%7C.5%C3
// * &port=26715
// * &natmapped=1
// * &localip=192.168.0.114
// * &port_type=lan
// * &uploaded=0
// * &downloaded=1966080
// * &left=375390208
// * &numwant=200
// * &compact=1
// * &no_peer_id=1
// * &key=5845
// * &event=stopped
// * 2022-03-15 11:00:13	Start connecting...
// * 2022-03-15 11:00:13	Tracker stopped
// */
