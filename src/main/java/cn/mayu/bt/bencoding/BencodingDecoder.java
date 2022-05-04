package cn.mayu.bt.bencoding;

import java.util.ArrayList;
import java.util.List;

/**
 * Bencoding 解码
 * 例子：
 * <br/>
 * d8:announce49:udp://tracker.leechers-paradise.org:6969/announce13:announce-listll49:udp://tracker.leechers-paradise.org:6969/announceel48:udp://tracker.internetwarriors.net:1337/announceeee
 * <br/>
 * 起始下标 结束下标 内容
 * <br/>
 * 0      0       d
 * <br/>
 * 1      10      8:announce
 * <br/>
 * 11     62      49:udp://tracker.leechers-paradise.org:6969/announce
 * <br/>
 * 63     78      13:announce-list
 * <br/>
 * 79     79      l
 * <br/>
 * 80     80      l
 * <br/>
 * 81     132     49:udp://tracker.leechers-paradise.org:6969/announce
 * <br/>
 * 133    133     e
 * <br/>
 * 134    134     l
 * <br/>
 * 135    185     48:udp://tracker.internetwarriors.net:1337/announce
 * <br/>
 * 186    186     e
 * <br/>
 * 187    187     e
 * <br/>
 * 189    189     e
 */
public class BencodingDecoder {

    private static final char DICTIONARY_START = 'd';

    private static final char LIST_START = 'l';

    private static final char NUMBER_START = 'i';

    private static final char END = 'e';

    private static final char STR_SEPARATOR = ':';

    public static List<BencodingData> parse(byte[] data) throws BencodingParseException {
        int offset = 0;
        List<BencodingData> dataList = new ArrayList<>();
        while (offset < data.length) {
            Tuple<BencodingData, Integer> tuple = doParse(data, offset);
            dataList.add(tuple.getData());
            offset = tuple.getOffset();
        }

        return dataList;
    }

    private static Tuple<BencodingData, Integer> doParse(byte[] data, int offset) throws BencodingParseException {
        if (data[offset] == DICTIONARY_START) {
            return parseDictionary(data, offset);
        }

        if (data[offset] == LIST_START) {
            return parseList(data, offset);
        }

        if (data[offset] == NUMBER_START) {
            return parseNum(data, offset);
        }

        return parseStr(data, offset);
    }

    private static int indexOf(byte[] data, int offset, int ch) throws BencodingParseException {
        for (int i = offset; i < data.length; i++) {
            if (data[i] == ch) {
                return i;
            }
        }

        throw new BencodingParseException(ch + " not found from " + offset + " to " + data.length + " in " + new String(data));
    }

    private static Tuple<BencodingData, Integer> parseStr(byte[] data, int offset) throws BencodingParseException {
        int segmentation = indexOf(data, offset, STR_SEPARATOR);
        try {
            int len = Integer.parseInt(new String(data, offset, segmentation - offset));
            byte[] newArr = new byte[len];
            System.arraycopy(data, segmentation + 1, newArr, 0, len);
            return new Tuple<>(new StringBencodingData(newArr), segmentation + len + 1);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new BencodingParseException(e.getMessage());
        }
    }

    private static Tuple<BencodingData, Integer> parseNum(byte[] data, int offset) throws BencodingParseException {
        int end = indexOf(data, offset, END);
        try {
            BencodingData d = new NumberBencodingData(Long.parseLong(new String(data, offset + 1, end - offset - 1)));
            return new Tuple<>(d, end + 1);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new BencodingParseException(e.getMessage());
        }
    }

    private static Tuple<BencodingData, Integer> parseList(byte[] data, int offset) throws BencodingParseException {
        ListBencodingData lbd = new ListBencodingData();
        offset++;
        while (data[offset] != END) {
            Tuple<BencodingData, Integer> tuple = doParse(data, offset);
            offset = tuple.getOffset();
            lbd.add(tuple.getData());
        }

        return new Tuple<>(lbd, ++offset);
    }

    private static Tuple<BencodingData, Integer> parseDictionary(byte[] data, int offset) throws BencodingParseException {
        DictionaryBencodingData dbd = new DictionaryBencodingData();
        BencodingData key = null;
        offset++;
        while (data[offset] != END) {
            Tuple<BencodingData, Integer> tuple = doParse(data, offset);
            offset = tuple.getOffset();
            if (key == null) {
                key = tuple.getData();
                continue;
            }

            dbd.put(key.toString(), tuple.getData());
            key = null;
        }

        return new Tuple<>(dbd, ++offset);
    }
}