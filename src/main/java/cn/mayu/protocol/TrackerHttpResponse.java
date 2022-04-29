package cn.mayu.protocol;

import cn.mayu.coding.BencodingData;
import cn.mayu.coding.BencodingDecoder;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * http get Tracker响应参数
 */
public class TrackerHttpResponse {

    // 请求为什么会失败
    private String failureReason;

    // 警告信息 不影响后续请求
    private String warningMessage;

    // 客户端每隔一定间隔(秒)就会向Tracker发送一个请求
    private long interval;

    // 最小的请求间隔，它表示客户端不能在这个时间间隔之内向Tracker重发请求
    private long minInterval;

    // 客户端发送其下一个请求时必须返回给Tracker的一个字符串。如果缺失，但是上一个请求发送了tracker id，那么不要丢弃旧值，重复利用即可
    private String trackerId;

    // 完成整个文件下载的peers数，即做种者的数量
    private int complete;

    // 非做种的peers数(还没有完成该文件下载的peers数)，即“占他人便宜者”数
    private int incomplete;

    // 同request compact
    private int compact;

    // 不是使用上面描述的dictionary model，binary model下，该键(key)对应的值可以有多个六字节组成的字符串。
    // 这六个字节中的前四个是IP地址，后两个是端口号。IP地址和端口号均以网络字节序(大端)表示。
    // 如果是一个dictionary list(列表)，该list中的每一个dictionary都包含如下的键(key)
    // peer id：peer自己选择的用来标识自己的ID，上文在描述Tracker请求时已经说明
    // ip：peer的IP地址，可以是Ipv6/Ipv4/DNS name
    // port：peer的端口号
    private List<InetSocketAddress> peers;

    public TrackerHttpResponse(byte[] data) {
        List<BencodingData> result = new BencodingDecoder(data).parse();
        if (result == null || result.size() <= 0) {
            return;
        }

        BencodingData bencodingData = result.get(0);
        BencodingData fr = bencodingData.getFromDictionary("failure reason");
        if (fr != null) {
            this.failureReason = fr.toString();
        }

        BencodingData wm = bencodingData.getFromDictionary("warning message");
        if (wm != null) {
            this.warningMessage = wm.toString();
        }

        BencodingData itl = bencodingData.getFromDictionary("interval");
        if (itl != null) {
            this.interval = itl.getNumber().longValue();
        }

        BencodingData mItl = bencodingData.getFromDictionary("minInterval");
        if (mItl != null) {
            this.minInterval = mItl.getNumber().longValue();
        }

        BencodingData ti = bencodingData.getFromDictionary("tracker id");
        if (ti != null) {
            this.trackerId = ti.toString();
        }

        BencodingData cpe = bencodingData.getFromDictionary("complete");
        if (cpe != null) {
            this.complete = cpe.getNumber().intValue();
        }

        BencodingData ipe = bencodingData.getFromDictionary("incomplete");
        if (ipe != null) {
            this.incomplete = ipe.getNumber().intValue();
        }

        BencodingData cpt = bencodingData.getFromDictionary("compact");
        this.compact = (cpt != null) ? cpt.getNumber().intValue() : 1;

        BencodingData peerData = bencodingData.getFromDictionary("peers");
        if (peerData != null) {
            byte[] peerByte = peerData.getByteArray();
            if (this.compact == 1 && peerByte.length >= 6) {
                List<InetSocketAddress> list = new ArrayList<>();
                for (int a = 0; a < peerByte.length; a += 6) {
                    if (a + 6 >= peerByte.length) {
                        break;
                    }

                    String host = (peerByte[a] & 0xFF) + "."
                                + (peerByte[a + 1] & 0xFF) + "."
                                + (peerByte[a + 2] & 0xFF) + "."
                                + (peerByte[a + 3] & 0xFF);
                    int port = ((peerByte[a + 4] & 0xFF) * 256) | (peerByte[a + 5] & 0xFF);
                    list.add(new InetSocketAddress(host, port));
                }

                this.peers = list;
            }

            if (this.compact == 0) {
                System.out.println(bencodingData);
            }
        }
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getMinInterval() {
        return minInterval;
    }

    public void setMinInterval(long minInterval) {
        this.minInterval = minInterval;
    }

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public int getIncomplete() {
        return incomplete;
    }

    public void setIncomplete(int incomplete) {
        this.incomplete = incomplete;
    }

    public List<InetSocketAddress> getPeers() {
        return peers;
    }

    public void setPeers(List<InetSocketAddress> peers) {
        this.peers = peers;
    }

    @Override
    public String toString() {
        return "TrackerHttpResponse{" +
                "failureReason='" + failureReason + '\'' +
                ", warningMessage='" + warningMessage + '\'' +
                ", interval=" + interval +
                ", minInterval=" + minInterval +
                ", trackerId='" + trackerId + '\'' +
                ", complete=" + complete +
                ", incomplete=" + incomplete +
                ", compact=" + compact +
                ", peers=" + peers +
                '}';
    }
}
