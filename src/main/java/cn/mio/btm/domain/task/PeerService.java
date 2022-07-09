package cn.mio.btm.domain.task;

import cn.mio.btm.domain.torrent.TorrentDescriptor;
import java.util.Collection;

public interface PeerService {

    Collection<Peer> getPeers(Task task, TorrentDescriptor torrent);
}
