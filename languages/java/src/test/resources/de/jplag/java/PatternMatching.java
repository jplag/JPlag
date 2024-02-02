public class PatternMatchingManual {
    private static final record Test(int x) {
    }

    public void test() {
        Object a = new Test(1);
        if (a instanceof Test testA) {
        }
    }
}