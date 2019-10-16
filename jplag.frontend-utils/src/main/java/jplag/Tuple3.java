package jplag;

public class Tuple3<T, TT, TTT> {
    private T a;
    private TT b;
    private TTT c;

    public T getA() {
        return a;
    }

    public TT getB() {
        return b;
    }

    public TTT getC() {
        return c;
    }

    public Tuple3(T a, TT b, TTT c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}
