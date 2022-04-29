package cn.mayu.coding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 列表型数据
 */
public class ListBencodingData implements BencodingData {

    protected List<BencodingData> list = new ArrayList<>();

    @Override
    public Stream<BencodingData> getFromList() {
        return list.stream();
    }

    @Override
    public String toString() {
        return list.stream().map(BencodingData::toString).collect(Collectors.joining(","));
    }
}
