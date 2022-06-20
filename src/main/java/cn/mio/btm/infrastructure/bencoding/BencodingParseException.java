package cn.mio.btm.infrastructure.bencoding;

/**
 * 解析Bencoding异常
 */
public class BencodingParseException extends Exception {

    public BencodingParseException(String s) {
        super(s);
    }
}
