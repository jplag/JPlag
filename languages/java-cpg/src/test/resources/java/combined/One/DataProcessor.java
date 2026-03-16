// DataProcessor.java
//@author Anthropic Claude Sonnet 4.5
public class DataProcessor {
    private static final int MAX_SIZE = 1000;
    private static final int MIN_SIZE = 0;
    private boolean enabled = true;

    // Dead field: never read
    private String processorName = "DefaultProcessor";
    private int unusedCounter = 0;

    public void processData(int size) {
        System.out.println("Processing data of size: " + size);

        // Dead code: variable assigned but never used
        int temp = size * 2;

        if (size > 50) {
            handleLargeData(size);
        } else {
            handleSmallData(size);
        }

        // Dead code: condition always true based on context
        if (enabled) {
            System.out.println("Processor is enabled");
        } else {
            // Dead code
            disableProcessor();
            cleanupResources();
        }

        // Dead code: unreachable after method calls
        finalizeProcessing();
        return;
    }

    private void handleLargeData(int size) {
        System.out.println("Handling large dataset");

        // Dead code: contradictory conditions
        if (size > 100 && size < 50) {
            optimizeForSpeed();
        }
    }

    private void handleSmallData(int size) {
        System.out.println("Handling small dataset");

        // Dead code: loop with impossible condition
        for (int i = size; i > size; i++) {
            processItem(i);
        }
    }

    private void finalizeProcessing() {
        System.out.println("Finalizing processing");

        // Dead code: try-finally with unreachable code
        try {
            System.out.println("Final operations");
            return;
        } finally {
            // This executes, but code after return in try is dead
            System.out.println("Cleanup in finally");
        }
    }

    // Dead method: never called
    private void disableProcessor() {
        enabled = false;
        System.out.println("Processor disabled");
    }

    // Dead method: never called
    private void cleanupResources() {
        System.out.println("Cleaning up resources");
    }

    // Dead method: never called
    private void logProcessingComplete() {
        System.out.println("Processing complete");
    }

    // Dead method: never called
    private void optimizeForSpeed() {
        System.out.println("Optimizing for speed");
    }

    // Dead method: never called
    private void processItem(int item) {
        System.out.println("Processing item: " + item);
    }

    // Dead method: never called
    public void resetProcessor() {
        enabled = true;
        unusedCounter = 0;
    }

    // Dead method: never called
    private boolean validateSize(int size) {
        return size >= MIN_SIZE && size <= MAX_SIZE;
    }

    // Dead method with dead code inside
    private void complexDeadMethod() {
        int x = 10;

        // Dead code: condition always false
        if (x > 100) {
            System.out.println("X is large");

            // Nested dead code
            if (x < 50) {
                System.out.println("X is also small somehow");
            }
        }

        // Dead code: unreachable loop
        while (x < 5) {
            x++;
            System.out.println("Incrementing x");
        }
    }

    // Dead method: never called
    public String getProcessorInfo() {
        return "Processor: " + processorName + ", Status: " +
                (enabled ? "Enabled" : "Disabled");
    }

    // Dead method with multiple return paths
    private int calculatePriority(int value) {
        if (value < 0) {
            return 0;
        } else if (value < 10) {
            return 1;
        } else if (value < 50) {
            return 2;
        } else {
            return 3;
        }
    }
}
