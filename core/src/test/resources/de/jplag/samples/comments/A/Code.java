public class Code {
    public static String name = "A";
    private List<String> names;

    public static void main(String[] args) {
        // This is a fancy main method, cool, right?
    }

    public String getName() {
        return A.name;
    }

    public boolean plagiarizedFunction(String[] s) {
        for (int i = 0; i < s.length; i++) {
            if (s[i].equals("JPlag")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Oh no! it seems like this comment is a
     * one-of-a-kind plagiarized multi-line comment!
     */

    // This is also a really really long plagiarized single line comment!
}