package cn.mayu.torrent;

import cn.mayu.coding.BencodingData;
import cn.mayu.coding.BencodingDecoder;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 校验并构建bt描述文件
 */
public class TorrentDescriptorBuilder {

    private String getAnnounce(BencodingData data) {
        BencodingData announceBencodingData = data.getFromDictionary("announce");
        if (announceBencodingData == null) {
            return null;
        }

        String announce = announceBencodingData.toString();
        return announce.substring(0, announce.length() - 1);
    }

    private Long getCreationDate(BencodingData data) {
        BencodingData date = data.getFromDictionary("creation date");
        if (date == null) {
            return null;
        }

        return Long.parseLong(date.toString());
    }

    private String getEncoding(BencodingData data) {
        BencodingData encoding = data.getFromDictionary("encoding");
        if (encoding == null) {
            return null;
        }

        return encoding.toString();
    }

    private String getCreatedBy(BencodingData data) {
        BencodingData createdBy = data.getFromDictionary("created by");
        if (createdBy == null) {
            return null;
        }

        return createdBy.toString();
    }

    private List<String> getAnnounceList(BencodingData data) {
        BencodingData list = data.getFromDictionary("announce-list");
        if (list == null) {
            return null;
        }

        return list.getFromList()
                .flatMap(BencodingData::getFromList)
                .map(BencodingData::toString)
                .collect(Collectors.toList());
    }

    private byte[] getPieces(BencodingData data) {
        BencodingData pieces = data.getFromDictionary("pieces");
        if (pieces == null) {
            return null;
        }

        return pieces.toString().getBytes();
    }

    private Long getPieceLength(BencodingData data) {
        BencodingData len = data.getFromDictionary("piece length");
        if (len == null) {
            return null;
        }

        return Long.parseLong(len.toString());
    }

    private String getName(BencodingData data) {
        BencodingData name = data.getFromDictionary("name");
        if (name == null) {
            return null;
        }

        return name.toString();
    }

    private byte[] getInfoHash(byte[] fileData) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        StringBuilder builder = new StringBuilder();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for(byte b : fileData) {
            if (!builder.toString().endsWith("4:info")) {
                builder.append((char) b);
            } else {
                output.write(b);
            }
        }

        sha1.update(output.toByteArray(), 0, output.size() - 1);
        return sha1.digest();
    }

    public TorrentDescriptor build(byte[] fileData) {
        List<BencodingData> dataList = new BencodingDecoder(fileData).parse();
        if (dataList == null || dataList.size() <= 0) {
            throw new IllegalArgumentException("parse file data error");
        }

        BencodingData data = dataList.get(0);
        TorrentDescriptor torrentDescriptor = new TorrentDescriptor();
        torrentDescriptor.setAnnounce(getAnnounce(data));
        torrentDescriptor.setCreationDate(getCreationDate(data));
        torrentDescriptor.setEncoding(getEncoding(data));
        torrentDescriptor.setCreatedBy(getCreatedBy(data));
        torrentDescriptor.setAnnounceList(getAnnounceList(data));
        try {
            torrentDescriptor.setInfoHash(getInfoHash(fileData));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("parse info_hash error " + e.getMessage());
        }

        BencodingData infoData = data.getFromDictionary("info");
        if (infoData != null) {
            TorrentDescriptor.Info info = new TorrentDescriptor.Info();
            info.setPieces(getPieces(infoData));
            info.setPieceLength(getPieceLength(infoData));
            info.setName(getName(infoData));
            torrentDescriptor.setInfo(info);

            BencodingData filesData = infoData.getFromDictionary("files");
            if (filesData != null) {
                info.setFiles(filesData.getFromList()
                        .map(f -> {
                            TorrentDescriptor.File file = new TorrentDescriptor.File();
                            file.setLength(Long.parseLong(f.getFromDictionary("length").toString()));
                            file.setPath(f.getFromDictionary("path").getFromList().map(BencodingData::toString).collect(Collectors.toList()));
                            return file;
                        }).collect(Collectors.toList()));
            } else {
                BencodingData lenData = infoData.getFromDictionary("length");
                if (lenData != null) {
                    TorrentDescriptor.File file = new TorrentDescriptor.File();
                    file.setLength(Long.parseLong(lenData.toString()));
                    file.setPath(List.of(info.getName()));
                    info.setFiles(List.of(file));
                }
            }
        }

        return torrentDescriptor;
    }
}
