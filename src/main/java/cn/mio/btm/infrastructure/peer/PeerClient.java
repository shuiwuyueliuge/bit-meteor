package cn.mio.btm.infrastructure.peer;

import cn.mio.btm.domain.task.Peer;
import cn.mio.btm.domain.torrent.TorrentDescriptor;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * 管理多个{@link PeerConnection}
 */
public interface PeerClient {

    CompletableFuture<PeerConnection> connect(String peerId, byte[] infoHash) throws IOException;

    PeerResFuture bitfield(TorrentDescriptor torr) throws IOException;

    PeerResFuture request();

    void keepAlive();

    Peer getPeer();

    enum PeerProtocolTypeEnum {

        // choke 消息没有 payload, 其 length prefix 等于 1
        CHOKE((byte) 0),

        // unchoke 消息没有 payload, 其 length prefix 等于 1
        UN_CHOKE((byte) 1),

        // interested 消息没有 payload, 其 length prefix 等于 1
        INTERESTED((byte) 2),

        // not interested 消息没有 payload, 其 length prefix 等于 1
        NOT_INTERESTED((byte) 3),

        // have 消息的 payload 只包含一个整数, 该整数对应的是该终端刚刚接收完成并校验通过 SHA1 哈希的 piece 索引
        // (终端在接收到并校验完一个 piece 后, 就向它所知道的所有 peer 都发送 have 消息以宣示它拥有了这个 piece,
        // 其它终端接收到 have 消息之后便可以知道对方已经有该 piece 的文件数据了, 因而可以向其发送 request 消息来获取该 piece)
        HAVE((byte) 4),

        // bitfield 消息只作为对等结点进行通信时所发送的第一个消息
        // (即在握手完成之后, 其它类型消息发送之前), 若文件没有分片, 则不发送该消息,
        // 它的 Payload 是一个字节序列, 逻辑上是一个 bitmap 结构, 指示当前该终端已下载的文件分片,
        // 其中第一个字节的 8 位分别表示文件的前 8 个分片, 第二个字节的 8 位分别表示文件的第 9 至 16 个分片,
        // 以此类推, 已下载的分片对应的位的值为 1, 否则为 0, 由于文件分片数不一定是 8 的整数倍,
        // 所以最后一个分片可能有冗余的比特位, 对于这些冗余的比特位都设置为 0
        BIT_FIELD((byte) 5),

        // request 消息用于一方向另一方请求文件数据, 它的 Payload 含有 3 个字段, 分别是 index, begin 和 length.
        // 其中 index 指示文件分片的索引 (索引从 0 开始), begin 指示 index 对应的 piece 内的字节索引 (索引从 0 开始),
        // length 指定请求的长度, length 一般都取 2 的整数次幂, 现在所有的 BitTorrent 实现中,
        // length 的值都取 214214, 即 16 KB, BitTorrent 协议规定结点通过随机的顺序请求下载文件 piece
        REQUEST((byte) 6),

        // piece 消息是对 request 消息的响应, 即返回对应的文件片段, 它的 Payload 含有 3 个字段,
        // 分别是 index, begin 和 piece, 其中 index 和 begin 字段与 request 消息中的 index 与 begin 含义相同,
        // 而 piece 是所请求的文件片段
        PIECE((byte) 7),

        // cancel 消息与 request 消息的 Payload 字段完全相同, 但作用相反, 用于取消对应的下载请求
        CANCEL((byte) 8);

        private final byte type;

        PeerProtocolTypeEnum(byte type) {
            this.type = type;
        }

        public byte getType() {
            return type;
        }
    }
}
