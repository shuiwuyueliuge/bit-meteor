package cn.mio.btm.infrastructure.peer;

import cn.mio.btm.infrastructure.protocol.PeerHandshakeResponse;

public class PeerResponse {

    private final byte[] resData;

    public PeerResponse(byte[] resData) {
        this.resData = resData;
    }

    public PeerHandshakeResponse getHandshakeResponse() {
        return new PeerHandshakeResponse(resData);
    }
}
