package java;

import java.util.Arrays;

public class Loop {
    public static void main(String[] args) {
        int sum = 0;
        int n = 10;
        int[] a = new int[n];
        for(int i = 0; i < n; i++) {
            sum = sum + a[i];
        }

        return sum;
    }
}
