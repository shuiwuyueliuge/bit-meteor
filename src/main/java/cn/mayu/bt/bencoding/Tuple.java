package cn.mayu.bt.bencoding;

class Tuple<T1, T2> {

    private final T1 left;

    private final T2 right;

    Tuple(T1 left, T2 right) {
        this.left = left;
        this.right = right;
    }

    public T1 getData() {
        return left;
    }

    public T2 getOffset() {
        return right;
    }
}
