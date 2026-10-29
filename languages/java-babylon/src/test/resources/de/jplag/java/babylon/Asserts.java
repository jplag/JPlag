void main() {
    String var1 = "Hello";
    boolean var2 = true;
    assert var2;
    assert var1.length() < 3 : "This one should fail";
}
