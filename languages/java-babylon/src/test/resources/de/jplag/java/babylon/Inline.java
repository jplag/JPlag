void main() {
    IO.println(toInline("Hello"));
}

String toInline(String parameter) {
    return "length: " + parameter.length();
}
