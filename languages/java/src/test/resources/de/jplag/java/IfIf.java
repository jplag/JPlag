import java.util.Arrays;

public class IfElse1 {
    public static void main(String[] args) {
        if (args == null) {
            throw new IllegalArgumentException();
        }
        if (args.length > 1) {
            System.out.println(Arrays.toString(args));
        }
    }
}
