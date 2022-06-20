package cn.mio.btm.infrastructure.protocol;

import cn.mio.btm.infrastructure.bencoding.BencodingParseException;
import cn.mio.btm.infrastructure.log.LogFactory;
import cn.mio.btm.infrastructure.log.Logger;
import cn.mio.btm.infrastructure.util.HttpUtil;
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
