package cn.mio.btm.infrastructure.service;

import cn.mio.btm.domain.task.Peer;
import cn.mio.btm.domain.task.PeerService;
import cn.mio.btm.domain.task.PeerState;
import cn.mio.btm.domain.task.Task;
import cn.mio.btm.domain.torrent.TorrentDescriptor;
import cn.mio.btm.infrastructure.bencoding.BencodingParseException;
import cn.mio.btm.infrastructure.log.LogFactory;
import cn.mio.btm.infrastructure.log.Logger;
import cn.mio.btm.infrastructure.protocol.*;
import cn.mio.btm.infrastructure.util.StringUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PeerServiceImpl implements PeerService {

    private static final Logger LOG = LogFactory.getLogger(PeerServiceImpl.class);

    private final TrackerClient trackerClient;

    public PeerServiceImpl(TrackerClient trackerClient) {
        this.trackerClient = trackerClient;
    }

    @Override
    public Collection<Peer> getPeers(Task task, TorrentDescriptor torrent) {
        String host = torrent.getAnnounce();
        byte[] infoHash = torrent.getInfoHash();
        String peerId = task.getPeerId();
        long downloaded = task.getDownloaded();
        long left = torrent.getInfo().getLength();
        List<Peer> peers = new ArrayList<>();
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
        try {
            TrackerHttpResponse res = trackerClient.connectTracker(request, connTimeout, readTimeout);
            peers = res.getPeers().stream().map(address -> new Peer(address, PeerState.READY)).collect(Collectors.toList());
            request.setEvent("stopped");
            trackerClient.connectTracker(request, connTimeout, readTimeout);
        } catch (IOException | BencodingParseException e) {
            request.setCompact(0);
            request.setNoPeerId(0);
            try {
                TrackerHttpResponse res = trackerClient.connectTracker(request, connTimeout, readTimeout);
                peers = res.getPeers().stream().map(address -> new Peer(address, PeerState.READY)).collect(Collectors.toList());
                request.setEvent("stopped");
                trackerClient.connectTracker(request, connTimeout, readTimeout);
            } catch (IOException | BencodingParseException ex) {
                LOG.debug("[Peer Finder] connect " + host + " error, " + e.getMessage());
            }
        }

        LOG.debug("[Peer Finder] be found address list: " + peers);
        return peers;
    }
}
