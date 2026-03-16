public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from Project 1!");
        System.out.println("Factorial of 5: " + factorial(5));
    }

    private static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    //DeadCodeStart
    private static String reverse(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }
    //DeadCodeEnd
}
