package jplag;

public class Tuple2<T, TT> {
    private T a;
    private TT b;

    public T getA() {
        return a;
    }

    public TT getB() {
        return b;
    }

    public Tuple2(T a, TT b) {
        this.a = a;
        this.b = b;
    }
}
