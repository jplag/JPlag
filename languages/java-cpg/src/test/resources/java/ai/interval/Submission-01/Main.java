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

        // Step 2: Constrain y to [10, 50]
        y = Math.max(10, Math.min(y, 50));

        // Step 3: Compute derived values with known intervals
        int sum = x + y;  // [10, Integer.MAX_VALUE + 50]
        int product = y * 2;  // [20, 100]

        // Dead code: sum can never be negative
        if (sum < 0) {
            System.out.print("impossible_negative_sum");
            result = -1;
        }

        // Dead code: sum is always >= 10
        if (sum < 5) {
            System.out.print("impossible_small_sum");
            result = -2;
        }

        // Dead code: product is in [20, 100], cannot be > 150
        if (product > 150) {
            System.out.print("impossible_large_product");
            result2 = -3;
        }

        // Dead code: product is always >= 20
        if (product < 15) {
            System.out.print("impossible_small_product");
            result2 = -4;
        }

        // Step 4: Nested constraints with multiple branches
        int bounded = Math.max(25, Math.min(x, 75));  // [25, 75]
        int scaled = bounded * 3;  // [75, 225]

        if (scaled > 300) {
            // DEAD: scaled <= 225
            System.out.print("unreachable_scaled_high");
            result3 = -5;
        } else if (scaled < 50) {
            // DEAD: scaled >= 75
            System.out.print("unreachable_scaled_low");
            result3 = -6;
        } else {
            // Always taken: scaled in [75, 225]
            System.out.print("always_scaled_middle");
            result3 = scaled;
        }

        // Step 5: Complex arithmetic with overflow considerations
        int clamped = Math.max(0, Math.min(x, 1000));  // [0, 1000]
        int doubled = clamped + clamped;  // [0, 2000]
        int offset = doubled + 500;  // [500, 2500]

        if (offset < 400) {
            // DEAD: offset >= 500
            System.out.print("impossible_offset_low");
            result4 = -7;
        }

        if (offset > 3000) {
            // DEAD: offset <= 2500
            System.out.print("impossible_offset_high");
            result4 = -8;
        }

        // Step 6: Chained conditionals creating dead branches
        int rangeA = Math.max(100, x);  // [100, Integer.MAX_VALUE]
        int rangeB = Math.min(rangeA, 200);  // [100, 200]

        if (rangeB < 90) {
            // DEAD: rangeB >= 100
            System.out.print("impossible_rangeB_below_90");
            result = -9;
        } else if (rangeB > 250) {
            // DEAD: rangeB <= 200
            System.out.print("impossible_rangeB_above_250");
            result = -10;
        } else if (rangeB >= 100 && rangeB <= 200) {
            // Always taken
            System.out.print("always_rangeB_in_range");
            result = rangeB;
        } else {
            // DEAD: all cases covered above
            System.out.print("logically_impossible");
            result = -11;
        }

        // Step 7: Multiple dependent intervals
        int base = Math.max(50, Math.min(y, 100));  // [50, 50]
        int increment = base + 30;  // [80, 80]
        int multiplied = increment * 2;  // [160, 160]

        if (multiplied < 150) {
            // DEAD: multiplied >= 160
            System.out.print("impossible_multiplied_low");
            result2 = -12;
        }

        if (multiplied > 300) {
            // DEAD: multiplied <= 260
            System.out.print("impossible_multiplied_high");
            result2 = -13;
        }

        // Step 8: Dead code after proven inequality
        int positive = Math.abs(x) + 1;  // [1, Integer.MAX_VALUE + 1]

        if (positive <= 0) {
            // DEAD: positive is always >= 1
            System.out.print("impossible_non_positive");
            result3 = -14;
        }

        // Step 9: Dead branches with division constraints
        int divisor = Math.max(5, Math.min(y, 10));  // [10, 10]   | y is [10, 50]
        int quotient = 100 / divisor;  // [10, 10]

        if (quotient > 25) {
            // DEAD: quotient <= 20
            System.out.print("impossible_quotient_high");
            result4 = -15;
        }

        if (quotient < 8) {
            // DEAD: quotient >= 10
            System.out.print("impossible_quotient_low");
            result4 = -16;
        }

        // Final result computation
        result = result + quotient;     //[110, 210]
        result2 = result2 + multiplied;
        result3 = result3 + scaled;
        result4 = result4 + offset;
    }

}
