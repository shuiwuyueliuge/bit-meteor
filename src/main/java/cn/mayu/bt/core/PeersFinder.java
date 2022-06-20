//package cn.mayu.bt.core;
//
//import cn.mayu.bt.bencoding.BencodingParseException;
//import cn.mayu.bt.log.LogFactory;
//import cn.mayu.bt.log.Logger;
//import cn.mayu.bt.protocol.TrackerClient;
//import cn.mayu.bt.protocol.TrackerHttpRequest;
//import cn.mayu.bt.protocol.TrackerHttpResponse;
//import cn.mio.btm.infrastructure.util.StringUtil;
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.util.List;
//
///**
// * 查找peers
// */
//public class PeersFinder {
//
//    private static final Logger LOG = LogFactory.getLogger(PeersFinder.class);
//
//    private final TrackerClient trackerClient;
//
//    public PeersFinder(TrackerClient trackerClient) {
//        this.trackerClient = trackerClient;
//    }
//
//    public void findPeers(String host, byte[] infoHash, String peerId, long downloaded, long left) {
//        List<InetSocketAddress> isaList = null;
//        TrackerHttpRequest request = new TrackerHttpRequest();
//        request.setHost(host);
//        request.setInfoHash(infoHash);
//        request.setPeerId(peerId);
//        request.setPort(6881);
//        request.setUploaded(0L);
//        request.setDownloaded(downloaded);
//        request.setLeft(left);
//        request.setEvent("started");
//        request.setKey(StringUtil.getRandomString(4));
//        int connTimeout = 3000;
//        int readTimeout = 7000;
//        try {
//            TrackerHttpResponse res = trackerClient.connectTracker(request, connTimeout, readTimeout);
//            isaList = res.getPeers();
//            request.setEvent("stopped");
//            trackerClient.connectTracker(request, connTimeout, readTimeout);
//        } catch (IOException | BencodingParseException e) {
//            request.setCompact(0);
//            request.setNoPeerId(0);
//            try {
//                TrackerHttpResponse res = trackerClient.connectTracker(request, connTimeout, readTimeout);
//                isaList = res.getPeers();
//                request.setEvent("stopped");
//                trackerClient.connectTracker(request, connTimeout, readTimeout);
//            } catch (IOException | BencodingParseException ex) {
//                LOG.debug("[Peer Finder] connect " + host + " error, " + e.getMessage());
//            }
//        }
//
//        if (isaList != null && !isaList.isEmpty()) {
//            LOG.info("[Peer Finder] isa list: " + isaList);
//            EventBus.publish(new PeerFindEvent(peerId, infoHash, isaList));
//        }
//    }
//}