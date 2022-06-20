package cn.mio.btm.domain.torrent;

import cn.mio.btm.infrastructure.bencoding.*;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class TorrentBencodingDataVisitor implements BencodingDataVisitor {

    private final TorrentDescriptor.Builder builder;

    private final byte[] fileData;

    private long tempNum;

    private byte[] tempArr;

    private List<String> tempList;

    public TorrentBencodingDataVisitor(TorrentDescriptor.Builder builder, byte[] fileData) {
        this.builder = builder;
        this.fileData = fileData;
    }

    @Override
    public void visitDictionary(DictionaryBencodingData dictionary) {
        BencodingData announceData = dictionary.getFromDictionary("announce");
        if (announceData != null) {
            announceData.visit(this);
            builder.setAnnounce(new String(tempArr));
        }

        BencodingData creationDateData = dictionary.getFromDictionary("creation date");
        if (creationDateData != null) {
            creationDateData.visit(this);
            builder.setCreationDate(tempNum);
        }

        BencodingData encodingData = dictionary.getFromDictionary("encoding");
        if (encodingData != null) {
            encodingData.visit(this);
            builder.setEncoding(new String(tempArr));
        }

        BencodingData createdByData = dictionary.getFromDictionary("created by");
        if (createdByData != null) {
            createdByData.visit(this);
            builder.setCreatedBy(new String(tempArr));
        }

        BencodingData announceListData = dictionary.getFromDictionary("announce-list");
        if (announceListData != null) {
            announceListData.visit(this);
            builder.setAnnounceList(tempList);
        }

        builder.setInfoHash(getInfoHash(fileData));

        BencodingData infoData = dictionary.getFromDictionary("info");
        if (infoData != null) {
            infoData.visit(this);
        }

        BencodingData piecesData = dictionary.getFromDictionary("pieces");
        if (piecesData != null) {
            piecesData.visit(this);
            builder.setPieces(new String(tempArr).getBytes());
        }

        BencodingData piecesLenData = dictionary.getFromDictionary("piece length");
        if (piecesLenData != null) {
            piecesLenData.visit(this);
            builder.setPieceLength(tempNum);
        }

        BencodingData nameData = dictionary.getFromDictionary("name");
        if (nameData != null) {
            nameData.visit(this);
            String name = new String(tempArr);
            builder.setName(name);

            // 单个文件info中才会有length字段
            BencodingData lengthData = dictionary.getFromDictionary("length");
            if (lengthData != null) {
                lengthData.visit(this);
                builder.setFile(new TorrentDescriptor.File(List.of(name), tempNum));
            }
        }

        BencodingData filesData = dictionary.getFromDictionary("files");
        if (filesData != null) {
            filesData.visit(this);
        }

        BencodingData lengthData = dictionary.getFromDictionary("length");
        BencodingData pathData = dictionary.getFromDictionary("path");
        if (lengthData != null && pathData != null) {
            lengthData.visit(this);
            pathData.visit(this);
            long len = tempNum;
            List<String> path = tempList;
            builder.setFile(new TorrentDescriptor.File(path, len));
        }
    }

    @Override
    public void visitList(ListBencodingData list) {
        tempList = list.getFromList()
                .map(data -> {
                    data.visit(this);
                    return new String(tempArr).replace("\r", "");
                })
                .collect(Collectors.toList());
    }

    @Override
    public void visitNumber(NumberBencodingData number) {
        tempNum = number.getLong();
    }

    @Override
    public void visitString(StringBencodingData str) {
        tempArr = str.getByteArray();
    }

    private byte[] getInfoHash(byte[] fileData) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            StringBuilder builder = new StringBuilder();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            for (byte b : fileData) {
                if (!builder.toString().endsWith("4:info")) {
                    builder.append((char) b);
                } else {
                    output.write(b);
                }
            }

            sha1.update(output.toByteArray(), 0, output.size() - 1);
            return sha1.digest();
        } catch (NoSuchAlgorithmException e) {
           throw new IllegalArgumentException(e);
        }
    }
}
