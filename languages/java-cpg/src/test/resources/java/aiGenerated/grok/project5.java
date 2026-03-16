public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from Project 3!");
        //System.out.println("Factorial of 4: " + factorial(4));
        System.out.println("Reverse of 'hello': " + reverse("hello"));
    }

    //DeadCodeStart
    // (Verbatim copy of factorial from Project 1/2, additionally hidden here)
    private static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
    //DeadCodeEnd

    private static String reverse(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    //DeadCodeStart
    private static void unusedSorter(int[] arr) {
        // Unused bubble sort implementation
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
    //DeadCodeEnd
}
