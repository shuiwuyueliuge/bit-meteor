package cn.mio.btm.infrastructure.service;

import cn.mio.btm.domain.PeerIdGenerator;
import cn.mio.btm.infrastructure.util.StringUtil;

public class PeerIdGeneratorImpl extends PeerIdGenerator {

    @Override
    public String genAzureusStyle() {
        return "-qB1000-" + StringUtil.getRandomString(12);
    }

    @Override
    public String genShadowStyle() {
        return super.genShadowStyle();
    }

    @Override
    public String genSelfStyle() {
        return null;
    }
}
