package cn.mayu.protocol;

import java.io.IOException;

public interface TrackerClient {

    TrackerHttpResponse connectTracker(TrackerHttpRequest request, int connTimeout, int readTimeout) throws IOException;
}
