class IfElseWithNegatedCondition {

    public void function(String string) {
        if (!(string.length() == 10)) {
            System.out.println("'%s' is NOT of length 10.".formatted(string));
        } else {
            System.out.println("'%s' IS of length 10.".formatted(string));
        }
    }

    public static void main(String[] args) {
        new IfElseWithNegatedCondition().function("Fellow burled");
    }
}