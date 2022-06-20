package cn.mio.btm.infrastructure.protocol;

import org.apache.commons.codec.net.URLCodec;

/**
 * http get Tracker请求参数
 */
public class TrackerHttpRequest {

    private String host;

    // URL编码的20字节SHA1散列，这个散列是元信息文件中info键所对应的值的SHA1散列。注意info键所对应的值是一个B编码的dictionary
    private byte[] infoHash;

    // 使用URL编码的20字节串，用于标识客户端的唯一ID，由客户端启动时生成
    private String peerId;

    // 客户端正在监听的端口号。为BitTorrent协议保留的端口号是6881-6889
    private int port;

    // 客户端已经上传的总量(从客户端发送’started’事件到Tracker算起)，以十进制ASCII表示。大部分情况下是指已经下载的字节总数
    private long uploaded;

    // 已下载的字节总量(从客户端发送’started’事件到Tracker算起)，以十进制ASCII表示。大部分情况下是指已经下载的字节总数
    private long downloaded;

    // 客户端还没有下载的字节数，以十进制ASCII表示
    private long left;

    // 可选 如果设置为1，表示客户端接收压缩的响应包。这时peers列表将被peers串代替，在这个peers串中，
    // 每个peer占六个字节。这六个字节的前四个字节表示主机信息(以大端表示即以网络字节序)，
    // 后两个字节表示端口号(以大端表示即以网络字节序)。需要注意的是，
    // 为了节约带宽，有些Tracker只支持返回压缩的响应包，如果没有将compact设置为1，
    // Tracker将拒绝这个请求或者如果请求中不包括compact=0这个参数，Tracker将返回压缩的响应包
    private int compact = 1;

    // 可选 表示Tracker将省略peers dictionary中的id字段。如果启用compact，那么就会忽略这个选项,1表示不需要peer id
    private int noPeerId = 1;

    // 可选 如果指定的话，必须是started, completed, stopped和空(和不指定的意义一样)中的一个。
    // 如果一个请求不指定event，表明它只是每隔一定间隔发送的请求
    //   started：第一个发送到Tracker的请求其event值必须是该值
    //   stopped：如果正常关闭客户端，必须发送改事件到Tracker
    //   completed：如果下载完毕，必须发送改事件到Tracker。
    //     如果客户端启动之前，已经下载完成的话，则没有必要发送该事件。Tracker仅仅基于该事件增加已经完成的下载数
    private String event;

    // 可选 客户主机的IP地址，点分十进制IPv4或者RFC3513指定的十六进制Ipv6地址,可以不传
    private String ip;

    // 可选 客户端希望从Tracker接受到的peers数，如果省略，则默认是50个
    private String numwant;

    // 可选 不和其他用户共享的附加标识。当客户端IP地址改变时，可以使用该标识来证明自己的身份
    private String key;

    // 可选 如果之前的announce包含一个tracker id，那么当前的请求必须设置该参数
    private String trackerId;

    public String genUrl() {
        String template = "%s?info_hash=%s&peer_id=%s&port=%s&uploaded=%s&downloaded=%s&left=%s&compact=%s&no_peer_id=%s%s%s%s%s";
        return String.format(template, host, new String(URLCodec.encodeUrl(null, infoHash)), peerId, port, uploaded, downloaded, left, compact, noPeerId,
                event == null ? "" : "&event=" + event,
                ip == null ? "" : "&ip=" + ip,
                numwant == null ? "" : "&numwant=" + numwant,
                key == null ? "" : "&key=" + key,
                trackerId == null ? "" : "&trackerId=" + trackerId);
    }

    @Override
    public String toString() {
       return genUrl();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public byte[] getInfoHash() {
        return infoHash;
    }

    public void setInfoHash(byte[] infoHash) {
        this.infoHash = infoHash;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getUploaded() {
        return uploaded;
    }

    public void setUploaded(long uploaded) {
        this.uploaded = uploaded;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public long getLeft() {
        return left;
    }

    public void setLeft(long left) {
        this.left = left;
    }

    public int getCompact() {
        return compact;
    }

    public void setCompact(int compact) {
        this.compact = compact;
    }

    public int getNoPeerId() {
        return noPeerId;
    }

    public void setNoPeerId(int noPeerId) {
        this.noPeerId = noPeerId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNumwant() {
        return numwant;
    }

    public void setNumwant(String numwant) {
        this.numwant = numwant;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }
}
