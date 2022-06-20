package cn.mio.btm.infrastructure.bencoding;

/**
 * Bencoding 数据标记
 */
public interface BencodingData {

    void visit(BencodingDataVisitor visitor);
}
