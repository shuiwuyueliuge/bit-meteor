package cn.mayu.torrent;

import java.util.Arrays;
import java.util.List;

/**
 * bt文件描述符
 */
public class TorrentDescriptor {

    // torrent文件的创建时间，时间使用标准的UNIX时间格式
    private Long creationDate;

    // 制作torrent文件的程序的名称和版本
    private String createdBy;

    // 当info dictionary过大时，就需要对其分片(piece)，该编码就是用来生成分片的
    private String encoding;

    // tracker的announce URL
    private String announce;

    // announce的扩展
    private List<String> announceList;

    // torrent文件的dictionary
    private Info info;

    // bt文件字节码
    private byte[] infoHash;

    public static TorrentDescriptorBuilder builder() {
        return new TorrentDescriptorBuilder();
    }

    public byte[] getInfoHash() {
        return infoHash;
    }

    public void setInfoHash(byte[] infoHash) {
        this.infoHash = infoHash;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getAnnounce() {
        return announce;
    }

    public void setAnnounce(String announce) {
        this.announce = announce;
    }

    public List<String> getAnnounceList() {
        return announceList;
    }

    public void setAnnounceList(List<String> announceList) {
        this.announceList = announceList;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
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

        // 每一片(piece)的字节数
        private Long pieceLength;

        // 文件名
        private String name;

        // torrent文件中包含的文件列表
        private List<File> files;

        public byte[] getPieces() {
            return pieces;
        }

        public void setPieces(byte[] pieces) {
            this.pieces = pieces;
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

        public List<File> getFiles() {
            return files;
        }

        public void setFiles(List<File> files) {
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

    public static class File {

        // 文件名
        private List<String> path;

        // 文件的所占字节数
        private Long length;

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
            return "File{" +
                    "path='" + path + '\'' +
                    ", length=" + length +
                    '}';
        }
    }
}
