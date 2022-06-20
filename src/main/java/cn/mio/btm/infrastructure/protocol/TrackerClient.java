package cn.mio.btm.infrastructure.protocol;

import cn.mio.btm.infrastructure.bencoding.BencodingParseException;

import java.io.IOException;

public interface TrackerClient {

    TrackerHttpResponse connectTracker(TrackerHttpRequest request, int connTimeout, int readTimeout) throws IOException, BencodingParseException;
}
