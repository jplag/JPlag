void main() {
    int[] values = {1, 2, 3};
    for (int value : values) {
        IO.println(value);
    }
    for (int i = 0; i < values.length; i++) {
        IO.println(values[i]);
    }
    for (int a[] = {1, 2}, i = 0; i < a.length; i++) {
        IO.println(a[i]);
    }

    for (int value : List.of(1, 2, 3)) {
        IO.println(value);
    }
}
