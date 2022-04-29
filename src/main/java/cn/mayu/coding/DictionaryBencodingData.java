package cn.mayu.coding;

import java.util.HashMap;
import java.util.Map;

/**
 * 自典型数据
 */
public class DictionaryBencodingData implements BencodingData {

    protected Map<BencodingData, BencodingData> map = new HashMap<>();

    @Override
    public BencodingData getFromDictionary(Object content) {
        BencodingData key = null;
        if (content instanceof String) {
            return map.get(new StringBencodingData(((String) content).getBytes()));
        }

        if (content instanceof Number) {
            return map.get(new NumberBencodingData((Number) content));
        }

        return null;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
