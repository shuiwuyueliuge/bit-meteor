package cn.mayu.bt.protocol;

import cn.mayu.bt.util.StringUtil;

/**
 * Azureus风格
 */
public class AzureusPeerIdGenerator implements PeerIdGenerator {

    @Override
    public String genPeerId() {
        return "-qB1000-" + StringUtil.getRandomString(12);
    }
}
