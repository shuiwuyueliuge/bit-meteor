package cn.mio.btm.infrastructure.protocol;

import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;
import java.util.Objects;

/**
 * peer握手请求
 *
 * 对等结点在建立完传输层连接 (如 TCP) 之后, 便开始进行 Peer Protocol 的握手, 握手消息依次含有如下数据:
 *
 * pstrlen, 该值固定为 19 (十进制格式, 使用 4 字节大端字节序)
 * pstr, 该值为 "BitTorrent protocol" (BitTorrent 协议的关键字)
 * reserved, BT 协议的保留字段, 用于以后扩展用, 一般将这 8 字节全部设置为 0, 某些 BT 客户端没有正确实现协议或者使用了某种扩展而发送了不全为 0 的握手信息, 此时忽略即可
 * info_hash, 与请求 BT Tracker 时发送的 info_hash 参数值相同
 * peer_id, 与请求 BT Tracker 时发送的 peer_id 参数值相同
 * 即握手消息的格式为 <pstrlen><pstr><reserved><info_hash><peer_id>, 对等结点一方在传输层连接建立以后便发送握手信息, 另一方收到握手信息后也回复一个握手信息, 任何一方当收到非法的握手信息 (如 pstrlen 不等于 19, pstr 缺失或其值不是 "BitTorrent protocol", info_hash 不相等, peer_id 与预期值不同等) 应立即断开连接
 */
public class PeerHandshakeResponse {

    private static final byte P_STR_LEN = 19;

    private static final String P_STR = "BitTorrent protocol";

    private final byte pStrLen;

    private final String pStr;

    @SuppressWarnings("all")
    private final byte[] reserved;

    private final String peerId;

    private final byte[] infoHash;

    public PeerHandshakeResponse(byte[] b) {
        this.pStrLen = b[0];
        this.pStr = new String(b, 1, 19);
        this.reserved = new byte[8];
        System.arraycopy(b, 20, reserved, 0, reserved.length);
        this.infoHash = new byte[20];
        System.arraycopy(b, 28, infoHash, 0, infoHash.length);
        this.peerId = new String(b, 48, 20);
    }

    public byte[] getReserved() {
        return reserved;
    }

    public String getPeerId() {
        return peerId;
    }

    public byte[] getInfoHash() {
        return infoHash;
    }

    public byte getPStrLen() {
        return pStrLen;
    }

    public String getPStr() {
        return pStr;
    }

    public boolean validate(byte[] infoHash) {
        if (this.pStrLen != P_STR_LEN) {
            return false;
        }

        if (!Objects.equals(this.pStr, P_STR)) {
            return false;
        }

        return Objects.equals(Hex.encodeHexString(this.infoHash), Hex.encodeHexString(infoHash));
    }

    @Override
    public String toString() {
        return "PeerHandshakeResponse{" +
                "pStrLen=" + pStrLen +
                ", pStr='" + pStr + '\'' +
                ", reserved=" + Arrays.toString(reserved) +
                ", peerId='" + peerId + '\'' +
                ", infoHash=" + Arrays.toString(infoHash) +
                '}';
    }
}
