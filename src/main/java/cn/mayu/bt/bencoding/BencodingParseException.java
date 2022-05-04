package cn.mayu.bt.bencoding;

/**
 * 解析Bencoding异常
 */
public class BencodingParseException extends Exception {

    public BencodingParseException(String s) {
        super(s);
    }
}
