//package cn.mayu.bt.bencoding;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 自典型数据
// */
//public class DictionaryBencodingData implements BencodingData {
//
//    private final Map<String, BencodingData> map = new HashMap<>();
//
//    public void put(String k, BencodingData v) {
//        map.put(k, v);
//    }
//
//    public BencodingData getFromDictionary(String key) {
//        return map.get(key);
//    }
//
//    @Override
//    public String toString() {
//        return map.toString();
//    }
//
//    @Override
//    public void visit(BencodingDataVisitor visitor) {
//        visitor.visitDictionary(this);
//    }
//}
