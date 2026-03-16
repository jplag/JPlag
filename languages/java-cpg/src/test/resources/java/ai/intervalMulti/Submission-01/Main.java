package edu.kit.informatik;

/**
 * @author GitHub Copilot (Claude Sonnet 4.5)
 */
public final class Main {

    private static int result = 1;
    private static int result2 = 1;
    private static int result3 = 1;
    private static int result4 = 1;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);

        // Step 1: Constrain x to [0, Integer.MAX_VALUE]
        x = Math.abs(x);

        // Step 2: Constrain y to [10, 50] or [200, 300]
        if (y >= 100) {
            y = Math.max(200, Math.min(y, 300));
        } else {
            y = Math.max(10, Math.min(y, 50));
        }

        // Step 3: Compute derived values with known intervals
        int sum = x + y;        //[10, Integer.MAX_VALUE]
        int product = y * 2;    //[20, 100] or [400, 600]

        // Step 4: Dead code detection
        if (y > 3000) {
            //Dead Code
            product--;
        }

        // Final result computation
        result = x;
        result2 = y;
        result3 = result3 + sum;
        result4 = product;
    }

}
