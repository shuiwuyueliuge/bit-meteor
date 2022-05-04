package cn.mayu.bt.protocol;

import cn.mayu.bt.bencoding.BencodingParseException;
import cn.mayu.bt.log.LogFactory;
import cn.mayu.bt.log.Logger;
import cn.mayu.bt.util.HttpUtil;
import java.io.IOException;

/**
 *
 */
public class DefaultTrackerClient implements TrackerClient {

    private static final Logger LOG = LogFactory.getLogger(DefaultTrackerClient.class);

    @Override
    public TrackerHttpResponse connectTracker(TrackerHttpRequest request, int connTimeout, int readTimeout) throws IOException, BencodingParseException {
        LOG.debug("[Tracker Client] request: " + request);
        byte[] res = HttpUtil.get(request.genUrl(), connTimeout, readTimeout);
        TrackerHttpResponse response = new TrackerHttpResponse(res);
        LOG.debug("[Tracker Client] response: " + response);
        return response;
    }
}
