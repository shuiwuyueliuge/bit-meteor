package cn.mio.btm.infrastructure.peer;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class PeerConnectionImpl implements PeerConnection {

    private final SocketChannel channel;

    public PeerConnectionImpl(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
