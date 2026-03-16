// UtilityHelper.java
//@author Anthropic Claude Sonnet 4.5
public class UtilityHelper {
    private static final boolean FEATURE_ENABLED = false;
    private static final int THRESHOLD = 100;

    // Dead fields: never used
    private String helperName = "UtilityHelper";
    private int operationCount = 0;
    private double multiplier = 1.5;

    public void performOperations() {
        System.out.println("Performing utility operations");

        // Dead code: constant condition
        if (FEATURE_ENABLED) {
            // Dead code block
            advancedOperation();
            complexCalculation();
        } else {
            System.out.println("Feature is disabled");
        }

        // Dead code: variable assigned but overwritten without use
        int result = 100;
        result = 200;
        result = 300;
        System.out.println("Result: " + result);

        // Dead code: unreachable due to exception
        try {
            System.out.println("Try block");
            throw new RuntimeException("Test");
        } catch (RuntimeException e) {
            System.out.println("Caught exception: " + e.getMessage());
        }

        // Dead code in switch with return
        switchOperation(5);
    }

    private void switchOperation(int value) {
        switch (value) {
            case 1:
                System.out.println("One");
                return;
            case 5:
                System.out.println("Five");
                break;
            default:
                System.out.println("Default");
                return;
        }

        System.out.println("After switch");
    }

    // Dead method: never called
    private void advancedOperation() {
        System.out.println("Advanced operation");

        // Dead code: nested dead conditions
        if (THRESHOLD > 200) {
            if (THRESHOLD < 150) {
                System.out.println("Impossible condition");
            }
        }
    }

    // Dead method: never called
    private int complexCalculation() {
        int a = 10, b = 20, c = 30;

        // Dead code: result never used
        int temp1 = a + b;
        int temp2 = b + c;
        int temp3 = a + c;

        return a + b + c;
    }

    // Dead method: never called
    private void cleanupOperation() {
        System.out.println("Cleanup");
    }

    // Dead method: never called
    public void performBatchOperation(int count) {
        for (int i = 0; i < count; i++) {
            processSingleItem(i);

            // Dead code: break in last iteration condition
            if (i == count - 1) {
                break;
            }
        }

        // Dead code: return at end of void method
        return;
    }

    // Dead method: never called
    private void processSingleItem(int item) {
        System.out.println("Processing: " + item);

        // Dead code: continue in single-statement loop
        for (int i = 0; i < 1; i++) {
            continue;
        }
    }

    // Dead method with multiple dead code patternsperformDeadCalculation
    private void multipleDeadPatterns() {
        // Pattern 1: Unreachable after return
        if (Math.random() > 0.5) {
            return;
        } else {
            return;
        }
    }

    // Dead method: never called
    public boolean validateThreshold(int value) {
        return value > THRESHOLD;
    }

    // Dead method: never called
    private String formatOutput(String input) {
        if (input == null) {
            return "null";
        }

        if (input.isEmpty()) {
            return "empty";
        }

        return input.trim().toUpperCase();
    }

    // Dead method: never called
    public void recursiveDeadCode(int n) {
        if (n <= 0) {
            return;
        }

        recursiveDeadCode(n - 1);

        // Dead code: infinite recursion prevention that's never needed
        if (n > 1000) {
            return;
        }
    }

    // Dead method: never called
    private void exceptionDeadCode() {
        try {
            System.out.println("Try block");
            // No exception thrown
        } catch (ArithmeticException e) {
            // Dead catch: specific exception never occurs
            System.out.println("Arithmetic exception");
        } catch (NullPointerException e) {
            // Dead catch: specific exception never occurs
            System.out.println("Null pointer exception");
        } finally {
            System.out.println("Finally block");
            return;
        }
    }
}
