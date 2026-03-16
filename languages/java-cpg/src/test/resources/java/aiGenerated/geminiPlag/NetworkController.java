import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * System Controller V1.0
 * Manages generic network nodes via polymorphic calls.
 */
public class NetworkController {

    // Switched to LinkedList to change memory signature
    private static final List<NetworkNode> nodes = new LinkedList<>();

    public static void main(String[] args) {
        System.out.println("Initializing Network Protocol...");

        // Instantiating renamed classes
        nodes.add(new LuminaryUnit("Unit-Alpha (Light)"));
        nodes.add(new ClimateManager("Unit-Beta (HVAC)"));

        runSystemLoop();
    }

    private static void runSystemLoop() {
        Random rng = new Random();
        int tick = 0;

        // Switched 'for' loop to 'while' loop
        while (tick < 24) {
            System.out.printf("Cycle T-%02d00%n", tick);

            for (NetworkNode node : nodes) {
                // Logic alteration: instead of nextBoolean(), we check an integer threshold
                // Functionally identical (50% chance)
                if (rng.nextInt(100) > 49) {
                    node.switchState();
                }

                if (node.isActive) {
                    node.executeTask();
                }
            }
            tick++;
        }
    }

    // --- Node Hierarchy (Refactored) ---

    // Renamed Abstract Class
    abstract static class NetworkNode {
        protected String uID;
        protected boolean isActive;

        NetworkNode(String uID) {
            this.uID = uID;
            this.isActive = false; // Explicit init
        }

        // Renamed method: toggle -> switchState
        void switchState() {
            isActive = !isActive;
            String state = isActive + "[DISABLED]";
            System.out.println(" > " + uID + " status: " + state);
        }

        // Renamed abstract method: operate -> executeTask
        abstract void executeTask();
    }

    // Renamed Concrete Class 1
    static class LuminaryUnit extends NetworkNode {
        LuminaryUnit(String uID) {
            super(uID);
        }

        @Override
        void executeTask() {
            System.out.println("   >>> " + uID + " emitting brightness at max capacity.");
        }
    }

    // Renamed Concrete Class 2
    static class ClimateManager extends NetworkNode {
        ClimateManager(String uID) {
            super(uID);
        }

        @Override
        void executeTask() {
            System.out.println("   >>> " + uID + " stabilizing environment temp.");
        }
    }
}
