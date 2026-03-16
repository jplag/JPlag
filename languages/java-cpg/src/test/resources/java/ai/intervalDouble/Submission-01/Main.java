package edu.kit.informatik;

/**
 * @author GitHub Copilot (Claude Sonnet 4.5)
 */
public final class Main {

    private static double result = 0.5f;
    private static double result2 = 0.5f;
    private static double result3 = 0.5f;
    private static double result4 = 0.5f;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);

        // Step 1: Constrain x to [0.0, Float.MAX_VALUE]
        x = Math.abs(x);

        // Step 2: Constrain y to [5.5, 25.5]
        y = Math.max(5.5f, Math.min(y, 25.5f));

        // Step 3: Compute derived values with known intervals
        double sum = x + y;  // [5.5, Float.MAX_VALUE + 25.5]
        double product = y * 0.5f;  // [2.75, 12.75]

        // Dead code: sum can never be negative
        if (sum < 0.0f) {
            System.out.print("impossible_negative_sum");
            result = -0.5f;
        }

        // Dead code: sum is always >= 5.5
        if (sum < 2.5f) {
            System.out.print("impossible_small_sum");
            result = -1.5f;
        }

        // Dead code: product is in [2.75, 12.75], cannot be > 15.0
        if (product > 15.0f) {
            System.out.print("impossible_large_product");
            result2 = -2.5f;
        }

        // Dead code: product is always >= 2.75
        if (product < 1.5f) {
            System.out.print("impossible_small_product");
            result2 = -3.5f;
        }

        // Step 4: Nested constraints with multiple branches
        double bounded = Math.max(12.5f, Math.min(x, 37.5f));  // [12.5, 37.5]
        double scaled = bounded * 1.5f;  // [18.75, 56.25]

        if (scaled > 60.0f) {
            // DEAD: scaled <= 56.25
            System.out.print("unreachable_scaled_high");
            result3 = -4.5f;
        } else if (scaled < 15.0f) {
            // DEAD: scaled >= 18.75
            System.out.print("unreachable_scaled_low");
            result3 = -5.5f;
        } else {
            // Always taken: scaled in [18.75, 56.25]
            System.out.print("always_scaled_middle");
            result3 = scaled;
        }

        // Step 5: Complex arithmetic with decimal operations
        double clamped = Math.max(0.0f, Math.min(x, 500.0f));  // [0.0, 500.0]
        double doubled = clamped * 2.0f;  // [0.0, 1000.0]
        double offset = doubled + 9.8f;  // [9.8, 1009.8]

        if (offset < 5.0f) {
            // DEAD: offset >= 9.8
            System.out.print("impossible_offset_low");
            result4 = -6.5f;
        }

        if (offset > 1500.0f) {
            // DEAD: offset <= 1009.8
            System.out.print("impossible_offset_high");
            result4 = -7.5f;
        }

        // Step 6: Chained conditionals creating dead branches
        double rangeA = Math.max(50.5f, x);  // [50.5, Float.MAX_VALUE]
        double rangeB = Math.min(rangeA, 100.5f);  // [50.5, 100.5]

        if (rangeB < 45.0f) {
            // DEAD: rangeB >= 50.5
            System.out.print("impossible_rangeB_below_45");
            result = -8.5f;
        } else if (rangeB > 125.0f) {
            // DEAD: rangeB <= 100.5
            System.out.print("impossible_rangeB_above_125");
            result = -9.5f;
        } else if (rangeB >= 50.5f && rangeB <= 100.5f) {
            // Always taken
            System.out.print("always_rangeB_in_range");
            result = rangeB;
        } else {
            // DEAD: all cases covered above
            System.out.print("logically_impossible");
            result = -10.5f;
        }

        // Step 7: Multiple dependent intervals with division
        double base = Math.max(25.0f, Math.min(y, 50.0f));  // [25.0, 25.5]
        double increment = base + 15.3f;  // [40.3, 40.8]
        double divided = increment / 2.0f;  // [20.15, 20.4]

        if (divided < 15.0f) {
            // DEAD: divided >= 20.4
            System.out.print("impossible_divided_low");
            result2 = -11.5f;
        }

        if (divided > 30.0f) {
            // DEAD: divided <= 20.4
            System.out.print("impossible_divided_high");
            result2 = -12.5f;
        }

        // Step 8: Dead code after proven inequality
        double positive = Math.abs(x) + 0.5f;  // [0.5, Float.MAX_VALUE]

        if (positive <= 0.0f) {
            // DEAD: positive is always >= 0.5
            System.out.print("impossible_non_positive");
            result3 = -13.5f;
        }

        // Step 9: Dead branches with modulo and division constraints
        double divisor = Math.max(2.5f, Math.min(y, 5.0f));  // [5.0, 5.0]   | y is [5.5, 25.5]
        double quotient = 50.0f / divisor;  // [10.0, 10.0]

        if (quotient > 12.5f) {
            // DEAD: quotient <= 9.09
            System.out.print("impossible_quotient_high");
            result4 = -14.5f;
        }

        if (quotient < 4.0f) {
            // DEAD: quotient >= 9.09
            System.out.print("impossible_quotient_low");
            result4 = -15.5f;
        }

        // Final result computation
        result = result + quotient;     // [10.0, 10.0] + [50.5, 100.5]
        result2 = result2 + divided;    // [20.65, 20.9]
        result3 = result3 + scaled;     // [37,5, 112,5]
        result4 = result4 + offset;     // [10.3, 1010.3]
    }

}
