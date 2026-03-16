public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from Project 2!");
        System.out.println("Reverse of 'abc': " + reverse("abc"));
    }

    private static String reverse(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    //DeadCodeStart
    // (Verbatim copy of factorial from Project 1, hidden here)
    private static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
    //DeadCodeEnd
}
