package cn.mayu.coding;

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

    private final byte[] data;

    // 结束下标的值
    private int offset;

    public BencodingDecoder(byte[] data) {
        this.data = data;
        this.offset = 0;
    }

    public List<BencodingData> parse() {
        List<BencodingData> l = new ArrayList<>();
        while (offset < data.length) {
            BencodingData r = doParse();
            if (r != null) {
                l.add(r);
            }

            offset++;
        }

        return l;
    }

    private BencodingData doParse() {
        if (data[offset] == 'd') {
            return parseDictionary();
        }

        if (data[offset] == 'l') {
            return parseList();
        }

        if (data[offset] == 'i') {
            return parseNum();
        }

        return parseStr();
    }

    private int indexOf(int ch) {
        for (int i = offset; i < data.length; i++) {
            if (data[i] == ch) {
                return i;
            }
        }

        return -1;
    }

    private BencodingData parseStr() {
        int segmentation = indexOf(':');
        if (segmentation == -1) {
            return null;
        }

        try {
            int len = Integer.parseInt(new String(data, offset, segmentation - offset));
            byte[] newArr = new byte[len];
            System.arraycopy(data, segmentation + 1, newArr, 0, len);
            this.offset = segmentation + len;
            return new StringBencodingData(newArr);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    private BencodingData parseNum() {
        int end = indexOf('e');
        if (end == -1) {
            return null;
        }

        try {
            BencodingData d = new NumberBencodingData(Long.parseLong(new String(data, offset + 1, end - offset - 1)));
            this.offset = end;
            return d;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    private BencodingData parseList() {
        ListBencodingData lbd = new ListBencodingData();
        while (data[this.offset + 1] != 'e') {
            offset++;
            BencodingData bd = doParse();
            if (bd != null) {
                lbd.list.add(bd);
            }
        }

        offset++;
        return lbd;
    }

    private BencodingData parseDictionary() {
        DictionaryBencodingData dbd = new DictionaryBencodingData();
        BencodingData key = null;
        while (data[this.offset + 1] != 'e') {
            offset++;
            BencodingData res = doParse();
            if (key == null) {
                key = res;
                continue;
            }

            dbd.map.put(key, res);
            key = null;
        }

        offset++;
        return dbd;
    }
}
