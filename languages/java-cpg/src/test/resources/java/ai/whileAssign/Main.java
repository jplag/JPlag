package edu.kit.informatik;

import java.util.Scanner;

public final class Main {

    private int result;
    private int result2;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int ponto = 0;
        int x = 1;

        while ((ponto = s.nextInt()) != 0) {
            x++;
        }

        result = x;
        result2 = ponto;
    }

}
