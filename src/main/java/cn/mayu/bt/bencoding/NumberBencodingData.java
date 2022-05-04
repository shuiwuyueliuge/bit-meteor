package cn.mayu.bt.bencoding;

import java.util.Objects;

/**
 * 整型数据
 */
public class NumberBencodingData implements BencodingData {

    private final Number data;

    public NumberBencodingData(Number data) {
        this.data = data;
    }

    public Long getLong() {
        return data.longValue();
    }

    public Integer getInt() {
        return data.intValue();
    }

    public Double getDouble() {
        return data.doubleValue();
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public Number getData() {
        return data;
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

    @Override
    public void visit(BencodingDataVisitor visitor) {
        visitor.visitNumber(this);
    }
}
