package cn.mio.btm.domain.torrent;

import java.util.Optional;

public interface TorrentDescriptorRepository {

    void save(TorrentDescriptor torrent);

    Optional<TorrentDescriptor> findById(String identity);

    void remove(String identity);
}
