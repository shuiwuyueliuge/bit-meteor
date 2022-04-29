package cn.mayu.protocol;

import cn.mayu.util.HttpUtil;
import java.io.IOException;

/**
 *
 */
public class DefaultTrackerClient implements TrackerClient {

    @Override
    public TrackerHttpResponse connectTracker(TrackerHttpRequest request, int connTimeout, int readTimeout) throws IOException {
        byte[] res = HttpUtil.get(request.genUrl(), connTimeout, readTimeout);
        return new TrackerHttpResponse(res);
    }
}
