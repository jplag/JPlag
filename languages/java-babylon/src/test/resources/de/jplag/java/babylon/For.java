void main() {
    for (int i = 0; i < 10;) {
        break;
    }

    for (int i = 0; true; i++) {
        if (i >= 10) break;
    }

    int[] values = {1, 2, 3, 4};
    for (int i = 0; i < values.length; i++) {
        IO.println(values[i]);
    }

    for (int value : values) {
        IO.println(value);
    }
}
