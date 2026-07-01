void main() {
    int a = Long.class.hashCode() % 10;
    int b = Double.class.hashCode() % 10;
    int c = Integer.class.hashCode() % 10;

    if (a > b) {
        if (b > c) {
            IO.println("A > C");
        }
    }

    if (a > b) {

    } else {
        if (b < c) {
            IO.println("A < C");
        }
    }


    if (a > b) {
        if (b > c) {
        }
    } else {
        if (b < c) {
            IO.println("A < C");
        }
    }

    if (a > b) {
        if (b > c) {
            IO.println("A > C");
        }
    } else {
        if (b < c) {
            IO.println("A < C");
        }
    }
}
