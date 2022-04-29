package cn.mayu.coding;

import java.util.Objects;

/**
 * 整型数据
 */
public class NumberBencodingData implements BencodingData {

    private final Number data;

    @Override
    public Long getNumber() {
        return (Long) data;
    }

    @Override
    public String toString() {
        return String.valueOf(data);
    }

    public NumberBencodingData(Number data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberBencodingData that = (NumberBencodingData) o;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
