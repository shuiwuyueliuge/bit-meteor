//package cn.mayu.bt.torrent;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * bt文件描述符
// */
//public class TorrentDescriptor {
//
//    // torrent文件的创建时间，时间使用标准的UNIX时间格式
//    private final Long creationDate;
//
//    // 制作torrent文件的程序的名称和版本
//    private final String createdBy;
//
//    // 当info dictionary过大时，就需要对其分片(piece)，该编码就是用来生成分片的
//    private final String encoding;
//
//    // tracker的announce URL
//    private final String announce;
//
//    // announce的扩展
//    private final List<String> announceList;
//
//    // torrent文件的dictionary
//    private final Info info;
//
//    // bt文件字节码
//    private final byte[] infoHash;
//
//    private TorrentDescriptor(
//            Long creationDate,
//            String createdBy,
//            String encoding,
//            String announce,
//            List<String> announceList,
//            Info info,
//            byte[] infoHash
//    ) {
//        this.creationDate = creationDate;
//        this.createdBy = createdBy;
//        this.encoding = encoding;
//        this.announce = announce;
//        this.announceList = announceList;
//        this.info = info;
//        this.infoHash = infoHash;
//    }
//
//    public static Builder builder() {
//        return new Builder();
//    }
//
//    public byte[] getInfoHash() {
//        return infoHash;
//    }
//
//    public Long getCreationDate() {
//        return creationDate;
//    }
//
//    public String getCreatedBy() {
//        return createdBy;
//    }
//
//    public String getEncoding() {
//        return encoding;
//    }
//
//    public String getAnnounce() {
//        return announce;
//    }
//
//    public List<String> getAnnounceList() {
//        return announceList;
//    }
//
//    public Info getInfo() {
//        return info;
//    }
//
//    @Override
//    public String toString() {
//        return "TorrentDescriptor{" +
//                "creationDate=" + creationDate +
//                ", createdBy='" + createdBy + '\'' +
//                ", encoding='" + encoding + '\'' +
//                ", announce='" + announce + '\'' +
//                ", announceList=" + announceList +
//                ", info=" + info +
//                '}';
//    }
//
//    public static class Info {
//
//        // 20字节SHA1散列值连接而成的字符串，每一片(piece)均含有一个唯一的SHA1散列值
//        private byte[] pieces;
//
//        // 每一片(piece)的字节数
//        private Long pieceLength;
//
//        // 文件名
//        private String name;
//
//        // torrent文件中包含的文件列表
//        private List<File> files;
//
//        public byte[] getPieces() {
//            return pieces;
//        }
//
//        public void setPieces(byte[] pieces) {
//            this.pieces = pieces;
//        }
//
//        public Long getPieceLength() {
//            return pieceLength;
//        }
//
//        public void setPieceLength(Long pieceLength) {
//            this.pieceLength = pieceLength;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public List<File> getFiles() {
//            return files;
//        }
//
//        public void setFiles(List<File> files) {
//            this.files = files;
//        }
//
//        @Override
//        public String toString() {
//            return "Info{" +
//                    "pieces=" + Arrays.toString(pieces) +
//                    ", pieceLength=" + pieceLength +
//                    ", name='" + name + '\'' +
//                    ", files=" + files +
//                    '}';
//        }
//    }
//
//    public static class File {
//
//        // 文件名
//        private List<String> path;
//
//        // 文件的所占字节数
//        private Long length;
//
//        public File(List<String> path, Long length) {
//            this.path = path;
//            this.length = length;
//        }
//
//        public File() {
//        }
//
//        public List<String> getPath() {
//            return path;
//        }
//
//        public void setPath(List<String> path) {
//            this.path = path;
//        }
//
//        public Long getLength() {
//            return length;
//        }
//
//        public void setLength(Long length) {
//            this.length = length;
//        }
//
//        @Override
//        public String toString() {
//            return "File{" +
//                    "path='" + path + '\'' +
//                    ", length=" + length +
//                    '}';
//        }
//    }
//
//    public static class Builder {
//
//        private Long creationDate;
//
//        private String createdBy;
//
//        private String encoding;
//
//        private String announce;
//
//        private List<String> announceList;
//
//        private Info info = new Info();
//
//        private byte[] infoHash;
//
//        public Builder setCreationDate(Long creationDate) {
//            this.creationDate = creationDate;
//            return this;
//        }
//
//        public Builder setCreatedBy(String createdBy) {
//            this.createdBy = createdBy;
//            return this;
//        }
//
//        public Builder setEncoding(String encoding) {
//            this.encoding = encoding;
//            return this;
//        }
//
//        public Builder setAnnounce(String announce) {
//            if (announce == null) {
//                throw new IllegalArgumentException("announce not be null");
//            }
//
//            if (announce.contains("\r")) {
//                announce = announce.replace("\r", "");
//            }
//
//            this.announce = announce;
//            return this;
//        }
//
//        public Builder setAnnounceList(List<String> announceList) {
//            this.announceList = announceList;
//            return this;
//        }
//
//        public Builder setInfo(Info info) {
//            this.info = info;
//            return this;
//        }
//
//        public Builder setInfoHash(byte[] infoHash) {
//            this.infoHash = infoHash;
//            return this;
//        }
//
//        public Builder setPieces(byte[] pieces) {
//            this.info.setPieces(pieces);
//            return this;
//        }
//
//        public Builder setPieceLength(Long pieceLength) {
//            this.info.setPieceLength(pieceLength);
//            return this;
//        }
//
//        public Builder setName(String name) {
//            this.info.setName(name);
//            return this;
//        }
//
//        public Builder setFile(File file) {
//            if (this.info.files == null) {
//                this.info.files = new ArrayList<>();
//            }
//
//            this.info.files.add(file);
//            return this;
//        }
//
//        public TorrentDescriptor build() {
//            return new TorrentDescriptor(
//                    creationDate,
//                    createdBy,
//                    encoding,
//                    announce,
//                    announceList,
//                    info,
//                    infoHash
//            );
//        }
//    }
//}
