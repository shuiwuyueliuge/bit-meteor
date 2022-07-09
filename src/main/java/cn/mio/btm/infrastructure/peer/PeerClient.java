package cn.mio.btm.infrastructure.peer;

import cn.mio.btm.domain.task.Peer;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface PeerClient {

    CompletableFuture<PeerConnection> connect(String peerId, byte[] infoHash) throws IOException;

    PeerResFuture bitfield();

    PeerResFuture request();

    void keepAlive();

    Peer getPeer();
}
