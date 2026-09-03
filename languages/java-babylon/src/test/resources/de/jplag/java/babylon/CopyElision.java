void main() {
    int a = 0;
    for (int i = 0; i < 10; i++) {
        a = i;
    }

    int b = 0;
    int c = b;
    IO.println(c);

    int d = IO.class.hashCode();
    int e = d;
    IO.println(e);

    for (int i = 0; i < 10; i++) {
        if (i < 5) {
            b++;
        } else {
            e++;
        }
    }
}
