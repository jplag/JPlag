public class Java21 {
    private static final record Circle(int radius) {
    }

    private static final record Rect(int width, int height) {
    }

    private static final record Both(Circle circle, Rect rect) {
    }

    private static final record Square(int length) {
    }

    public void main() {
        Shape s = new Circle();
        switch (s) {
            case Cricle(int r) -> System.out.println(r);
            case Shape s -> System.out.println(c);
            case Both(Circle c, Rect(int _, int w)) -> System.out.println("something");
            case Square -> {
                int l = ((Square) s).length;
                System.out.println("Square with length " + Math.abs(l));
            }
        }


        if (s instanceof Circle(int r)) {

        }
    }
}