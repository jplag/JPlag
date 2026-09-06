void main() {
    int result = condition() ? 5 : 10;

    int result2 = result > 5 ? result - 5 : result + 5;

    int result3;
    if (result2 > 5) {
        result3 = result2 - 5;
    } else {
        result3 = result2 + 5;
    }

    IO.println(result3);
}

boolean condition() {
    return true;
}
