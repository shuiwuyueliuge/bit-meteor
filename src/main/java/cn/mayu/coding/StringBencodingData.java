package cn.mayu.coding;

import java.util.Objects;

/**
 * 字符型数据
 */
public class StringBencodingData implements BencodingData {

    private final byte[] data;

    public StringBencodingData(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] getByteArray() {
        return data;
    }

    @Override
    public String toString() {
        return new String(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringBencodingData that = (StringBencodingData) o;
        return Objects.equals(that.toString(), toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }
}
