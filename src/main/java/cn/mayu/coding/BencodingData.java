package cn.mayu.coding;

import java.util.stream.Stream;

/**
 *
 */
public interface BencodingData {

    default byte[] getByteArray() {
        return null;
    }

    default Number getNumber() {
        return null;
    }

    default Stream<BencodingData> getFromList() {
        return null;
    }

    default BencodingData getFromDictionary(Object key) {
        return null;
    }
}
