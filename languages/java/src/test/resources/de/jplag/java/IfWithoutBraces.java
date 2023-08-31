package java;

import java.util.Arrays;

public class IfWithoutBraces {
    public static void main(String[] args) {
        if (args == null)
            throw new IllegalArgumentException();
        else if (args.length > 1)
            System.out.println(Arrays.toString(args));
        else
            System.out.println(args[0]);
    }
}
