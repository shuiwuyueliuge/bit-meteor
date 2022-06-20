package cn.mio.btm.infrastructure.bencoding;

public interface BencodingDataVisitor {

    default void visitDictionary(DictionaryBencodingData dictionary) {}

    default void visitList(ListBencodingData list) {}

    default void visitNumber(NumberBencodingData number) {}

    default void visitString(StringBencodingData str) {}
}
