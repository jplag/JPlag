void main() {
    IO.println(toInline2(toInline("Hello")));
}

String toInline(String parameter) {
    return "length: " + parameter.length();
}

String toInline2(String parameter) {
    if (parameter.length() > 5) {
        return parameter;
    }
    return "";
}
