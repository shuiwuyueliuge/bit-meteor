//package cn.mayu.bt.bencoding;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Stream;
//
///**
// * 列表型数据
// */
//public class ListBencodingData implements BencodingData {
//
//    private final List<BencodingData> list = new ArrayList<>();
//
//    public void add(BencodingData data) {
//        list.add(data);
//    }
//
//    public Stream<BencodingData> getFromList() {
//        return list.stream();
//    }
//
//    @Override
//    public String toString() {
//        return list.toString();
//    }
//
//    @Override
//    public void visit(BencodingDataVisitor visitor) {
//        visitor.visitList(this);
//    }
//}
