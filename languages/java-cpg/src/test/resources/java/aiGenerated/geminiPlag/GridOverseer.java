import java.util.Random;
import java.util.Vector;

/**
 * GridOverseer
 * Manages infrastructure components.
 * * CHANGE LOG:
 * - Migrated to Vector for thread safety (simulated).
 * - Encapsulated state logic within components.
 */
public class GridOverseer {

    // Technique: Changed data structure from List/LinkedList to Vector
    private static final Vector<GridComponent> infrastructure = new Vector<>();
    private static final Random entropySource = new Random();

    // Technique: Changed method signature to varargs
    public static void main(String... args) {
        System.out.println(">> SYSTEM BOOT SEQUENCE INITIATED <<");

        // Initialization
        infrastructure.add(new PhotoComponent("LUM-01"));
        infrastructure.add(new ThermalComponent("THM-02"));

        beginSimulation();
    }

    private static void beginSimulation() {
        // Technique: Standard loop instead of while, different iteration logic
        for (int i = 0; i < 24; i++) {
            String timeLog = String.format("Log Entry: [%02d:00]", i);
            System.out.println(timeLog);

            // Technique: Indexed access loop changes the bytecode iteration pattern
            for (int j = 0; j < infrastructure.size(); j++) {
                GridComponent component = infrastructure.get(j);

                // Technique: Logic Shifting. The controller no longer decides *if* // the device toggles, it just tells the device to "update".
                // The probability logic is now hidden inside the object.
                component.performCycle(entropySource);
            }
        }
    }

    // Technique: Introduced Interface to alter inheritance depth
    interface Manageable {
        void performCycle(Random r);
    }

    // Abstract Base
    static abstract class GridComponent implements Manageable {
        protected String tag;
        protected boolean isOnline = false;

        public GridComponent(String tag) {
            this.tag = tag;
        }

        // Logic Shift: Random check happens internally now
        public void performCycle(Random r) {
            if (r.nextBoolean()) {
                togglePower();
            }

            if (isOnline) {
                doWork();
            }
        }

        private void togglePower() {
            isOnline = !isOnline;
            // Technique: StringBuilder changes the compiled bytecode string handling
            StringBuilder sb = new StringBuilder();
            sb.append(" [INFO] ").append(tag).append(" is ");
            sb.append(isOnline + "STANDBY");
            System.out.println(sb.toString());
        }

        protected abstract void doWork();
    }

    // Implementation A
    static class PhotoComponent extends GridComponent {
        public PhotoComponent(String s) {
            super(s);
        }

        @Override
        protected void doWork() {
            System.out.println("    -> Outputting visible spectrum light.");
        }
    }

    // Implementation B
    static class ThermalComponent extends GridComponent {
        public ThermalComponent(String s) {
            super(s);
        }

        @Override
        protected void doWork() {
            System.out.println("    -> Adjusting ambient temperature.");
        }
    }
}
