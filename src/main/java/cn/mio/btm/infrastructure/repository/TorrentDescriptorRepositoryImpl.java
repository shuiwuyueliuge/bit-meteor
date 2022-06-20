package cn.mio.btm.infrastructure.repository;

import cn.mio.btm.domain.torrent.TorrentDescriptor;
import cn.mio.btm.domain.torrent.TorrentDescriptorRepository;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TorrentDescriptorRepositoryImpl implements TorrentDescriptorRepository {

    private final Map<String, TorrentDescriptor> torrentDescriptors;

    public TorrentDescriptorRepositoryImpl() {
        this.torrentDescriptors = new ConcurrentHashMap<>();
    }

    @Override
    public void save(TorrentDescriptor torrent) {
        torrentDescriptors.put(torrent.getIdentity(), torrent);
    }

    @Override
    public Optional<TorrentDescriptor> findById(String identity) {
        TorrentDescriptor cached = torrentDescriptors.get(identity);
        return Objects.isNull(cached) ? Optional.empty() : Optional.of(cached);
    }

    @Override
    public void remove(String identity) {
        torrentDescriptors.remove(identity);
    }
}
