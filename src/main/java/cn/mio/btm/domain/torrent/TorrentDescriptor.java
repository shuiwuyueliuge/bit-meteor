package cn.mio.btm.domain.torrent;

import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * bt文件描述符
 */
public class TorrentDescriptor {

    private final String identity;

    // torrent文件的创建时间，时间使用标准的UNIX时间格式
    private final Long creationDate;

    // 制作torrent文件的程序的名称和版本
    private final String createdBy;

    // 当info dictionary过大时，就需要对其分片(piece)，该编码就是用来生成分片的
    private final String encoding;

    // tracker的announce URL
    private final String announce;

    // announce的扩展
    private final List<String> announceList;

    // torrent文件的dictionary
    private final Info info;

    // bt文件字节码
    private final byte[] infoHash;

    private TorrentDescriptor(
            String identity,
            Long creationDate,
            String createdBy,
            String encoding,
            String announce,
            List<String> announceList,
            Info info,
            byte[] infoHash
    ) {
        this.identity = identity;
        this.creationDate = creationDate;
        this.createdBy = createdBy;
        this.encoding = encoding;
        this.announce = announce;
        this.announceList = announceList;
        this.info = info;
        this.infoHash = infoHash;
    }

    public String getIdentity() {
        return identity;
    }

    public static Builder builder() {
        return new Builder();
    }

    public byte[] getInfoHash() {
        return infoHash;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getAnnounce() {
        return announce;
    }

    public List<String> getAnnounceList() {
        return announceList;
    }

    public Info getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "TorrentDescriptor{" +
                "creationDate=" + creationDate +
                ", createdBy='" + createdBy + '\'' +
                ", encoding='" + encoding + '\'' +
                ", announce='" + announce + '\'' +
                ", announceList=" + announceList +
                ", info=" + info +
                '}';
    }

    public static class Info {

        // 20字节SHA1散列值连接而成的字符串，每一片(piece)均含有一个唯一的SHA1散列值
        private byte[] pieces;

        private BitSet finishRecord;

        // 每一片(piece)的字节数
        private Long pieceLength;

        // 文件名
        private String name;

        // torrent文件中包含的文件列表
        private List<MultiFile> files;

        // torrent文件中包含的单个文件
        private SingleFile singleFile;

        // 分片个数
        private int pieceSize;

        public long getLength() {
            return Objects.nonNull(singleFile) ? singleFile.getLength() : files.stream().mapToLong(MultiFile::getLength).sum();
        }

        public int getPieceSize() {
            return pieceSize;
        }

        public SingleFile getSingleFile() {
            return singleFile;
        }

        public void setSingleFile(SingleFile singleFile) {
            this.singleFile = singleFile;
        }

        public BitSet getFinishRecord() {
            return finishRecord;
        }

        public void pieceFinish(int pieceIndex) {
            this.finishRecord.set(pieceIndex);
        }

        public byte[] getPieces() {
            return pieces;
        }

        public void setPieces(byte[] pieces) {
            this.pieces = pieces;
            this.pieceSize = pieces.length / 20;
            int recordBucket = pieceSize / 8;
            if ((pieceSize & 8) != 0) {
                recordBucket++;
            }

            this.finishRecord = new BitSet(recordBucket);
        }

        public Long getPieceLength() {
            return pieceLength;
        }

        public void setPieceLength(Long pieceLength) {
            this.pieceLength = pieceLength;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<MultiFile> getFiles() {
            return files;
        }

        public void setFiles(List<MultiFile> files) {
            this.files = files;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "pieces=" + Arrays.toString(pieces) +
                    ", pieceLength=" + pieceLength +
                    ", name='" + name + '\'' +
                    ", files=" + files +
                    '}';
        }
    }

    /**
     * 例如:目标文件大小FileSpace为 1039143285 bytes,文件每个分片大小PerPieceSpace为 1048576 bytes,计算可得：
     * <p>
     * 1039143285 = 1048576 x 991 + 4469
     * <p>
     * 即 FileSpace= PerPieceSpace*991 + 4469
     * <p>
     * 目标文件按照指定大小分片后，为991个满足分片大小的分片文件和1个余数文件，总共是992个小文件。
     * <p>
     * 其存储的SHA1每个长度为20 bytes，进而可知pieces中存储的SHA1个数为：
     * NumberOfSHA1 = 19840/20 = 992
     * <p>
     * 即torrent文件的pieces中存储了992个SHA1值。这样每个小文件都对应上了一个SHA1校验值。
     */
    public static class SingleFile {

        private final Long length;

        public SingleFile(Long length) {
            this.length = length;
        }

        public Long getLength() {
            return length;
        }
    }

    public static class MultiFile {

        // 文件名
        private List<String> path;

        // 文件的所占字节数
        private Long length;

        public MultiFile(List<String> path, Long length) {
            this.path = path;
            this.length = length;
        }

        public List<String> getPath() {
            return path;
        }

        public void setPath(List<String> path) {
            this.path = path;
        }

        public Long getLength() {
            return length;
        }

        public void setLength(Long length) {
            this.length = length;
        }

        @Override
        public String toString() {
            return "MultiFile{" +
                    "path='" + path + '\'' +
                    ", length=" + length +
                    '}';
        }
    }

    public static class Builder {

        private Long creationDate;

        private String createdBy;

        private String encoding;

        private String announce;

        private List<String> announceList;

        private Info info = new Info();

        private byte[] infoHash;

        public Builder setCreationDate(Long creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder setEncoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public Builder setAnnounce(String announce) {
            if (announce == null) {
                throw new IllegalArgumentException("announce not be null");
            }

            if (announce.contains("\r")) {
                announce = announce.replace("\r", "");
            }

            this.announce = announce;
            return this;
        }

        public Builder setAnnounceList(List<String> announceList) {
            this.announceList = announceList;
            return this;
        }

        public Builder setInfo(Info info) {
            this.info = info;
            return this;
        }

        public Builder setInfoHash(byte[] infoHash) {
            this.infoHash = infoHash;
            return this;
        }

        public Builder setPieces(byte[] pieces) {
            this.info.setPieces(pieces);
            return this;
        }

        public Builder setPieceLength(Long pieceLength) {
            this.info.setPieceLength(pieceLength);
            return this;
        }

        public Builder setName(String name) {
            this.info.setName(name);
            return this;
        }

        public Builder setFile(MultiFile file) {
            if (this.info.files == null) {
                this.info.files = new ArrayList<>();
            }

            this.info.files.add(file);
            return this;
        }

        public Builder setSingleFile(SingleFile file) {
            this.info.singleFile = file;
            return this;
        }

        public TorrentDescriptor build() {
            return new TorrentDescriptor(
                    Hex.encodeHexString(infoHash),
                    creationDate,
                    createdBy,
                    encoding,
                    announce,
                    announceList,
                    info,
                    infoHash
            );
        }
    }
}
