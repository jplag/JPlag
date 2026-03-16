// Main.java
//@author Anthropic Claude Sonnet 4.5
public class Main {
    private static final boolean DEBUG = false;
    private static final String UNUSED_CONSTANT = "This is never used";
    private int unusedField = 42;

    public static void main(String[] args) {
        System.out.println("Advanced Dead Code Analysis Program");

        Main m = new Main();
        m.demonstrateDeadCode();

        DataProcessor processor = new DataProcessor();
        processor.processData(100);

        UtilityHelper helper = new UtilityHelper();
        helper.performOperations();

        // Dead code: condition always false
        if (false) {
            System.out.println("This will never execute");
            m.neverCalledMethod();
        }

        // Dead code: unreachable after return
        System.out.println("Program completed");
        return;
    }

    private void demonstrateDeadCode() {
        int x = 10;
        int y = 20;

        // Dead code: result never used
        int unused = x + y;

        // Dead code: variable assigned but never read
        String message = "Hello";
        message = "World";

        // Dead code: condition always true
        if (true) {
            System.out.println("This always executes");
        } else {
            System.out.println("This never executes");
            performDeadCalculation();
        }

        // Dead code: complex condition that's always false
        if (x > 100 && x < 50) {
            System.out.println("Logically impossible");
        }

        // Dead code: loop that never executes
        for (int i = 10; i < 5; i++) {
            System.out.println("Never runs: " + i);
        }

        // Dead code: switch case never reached
        int value = 1;
        switch (value) {
            case 1:
                System.out.println("Case 1");
                break;
            case 2:
                System.out.println("Case 2");
                break;
            default:
                break;
        }
    }

    // Dead method: never called
    private void neverCalledMethod() {
        System.out.println("This method is never invoked");
        int result = complexCalculation(5, 10);
    }

    // Dead method: never called
    private int complexCalculation(int a, int b) {
        return a * b + (a - b) * 2;
    }

    // Dead method: never called
    private void performDeadCalculation() {
        double pi = 3.14159;
        double radius = 5.0;
        double area = pi * radius * radius;
        System.out.println("Area: " + area);
    }

    // Dead method: never called
    private void anotherUnreachableMethod() {
        System.out.println("Unreachable");
    }

    // Dead method: never called
    public String formatMessage(String input) {
        return "[" + input.toUpperCase() + "]";
    }

    // Dead method: never called
    private boolean validateInput(int input) {
        return input > 0 && input < 1000;
    }

    // Dead code in method with early returns
    public int processValue(int value) {
        if (value < 0) {
            return -1;
        }

        if (value == 0) {
            return 0;
        }

        return value * 2;
    }

    // Dead code with exception handling
    public void methodWithDeadCatch() {
        try {
            System.out.println("Normal operation");
            // No exception thrown
        } catch (NullPointerException e) {
            // Dead code: this specific exception never occurs
            System.out.println("Handling null pointer");
        } catch (Exception e) {
            // Dead code: no exception is ever thrown
            System.out.println("General exception");
        }
    }
}
