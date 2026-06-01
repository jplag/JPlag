package java;

public class Loop {
    public static void main(String[] args) {
        int sum = 0;
        int n = 10;
        for (int i = 0; i < n; i++) {
            sum = sum + Integer.parseInt(args[i]);
        }
        return sum;
    }
}
