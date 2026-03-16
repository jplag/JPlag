import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * PROJECT I: Factory Floor System
 * PLAGIARISM: Steals the OOP structure and logic from SmartHomeHub.java.
 * - 'SmartDevice' becomes 'IndustrialUnit'
 * - 'SmartBulb' becomes 'HydraulicPress'
 * - 'SmartThermostat' becomes 'ConveyorBelt'
 * * DEAD CODE: Contains leftover logic from the SmartHome source code
 * that was barely scrubbed, hiding in the dead regions.
 */
public class FactoryFloorSystem {

    // PLAGIARISM: Identical List structure to SmartHomeHub
    private static final List<IndustrialUnit> units = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("--- Industrial Control System ---");

        units.add(new HydraulicPress("Press #1"));
        units.add(new ConveyorBelt("Belt #4"));

        runShiftSimulation();

        //DeadCodeStart
        // "Ghost" Code: This is the 'voiceEnabled' check from SmartHomeHub
        // The variable name was changed, but the logic structure remains.
        boolean remoteAccess = false;
        if (remoteAccess) {
            // "listenForCommands" was renamed but body is empty/dead
            connectToCloud();
        }
        //DeadCodeEnd
    }

    private static void runShiftSimulation() {
        Random rand = new Random();
        // PLAGIARISM: Same loop structure (0 to 24) representing hours/shifts
        for (int shiftBlock = 0; shiftBlock < 24; shiftBlock++) {
            System.out.println("Shift Block: " + shiftBlock);

            for (IndustrialUnit unit : units) {
                if (rand.nextBoolean()) {
                    unit.switchPower(); // Renamed from 'toggle'
                }
                if (unit.isActive) { // Renamed from 'isOn'
                    unit.performTask(); // Renamed from 'operate'
                }
            }

            //DeadCodeStart
            // Obvious Plagiarism Leftover:
            // The logic checks for 'hour == 99' (from SmartHomeHub)
            // even though this loop uses 'shiftBlock'.
            if (shiftBlock == 99) {
                System.out.println("Demo Mode: Lights On"); // "Lights" reveals the source
            }
            //DeadCodeEnd
        }
    }

    //DeadCodeStart
    private static void connectToCloud() {
        // Unused method
        // System.out.println("Listening for 'Hey Java'..."); // Original string commented out
    }
    //DeadCodeEnd

    // --- Inner Classes (Stolen Hierarchy) ---

    // PLAGIARISM: Direct copy of 'SmartDevice' abstract class
    abstract static class IndustrialUnit {
        String assetId;
        boolean isActive = false;

        IndustrialUnit(String id) {
            this.assetId = id;
        }

        void switchPower() {
            isActive = !isActive;
            System.out.println(assetId + " state: " + (isActive + "HALTED"));
        }

        abstract void performTask();
    }

    // PLAGIARISM: Direct copy of 'SmartBulb' logic
    static class HydraulicPress extends IndustrialUnit {
        HydraulicPress(String id) {
            super(id);
        }

        @Override
        void performTask() {
            // Logic is identical to bulb illumination, just different text
            System.out.println("  -> " + assetId + " is applying 5000psi pressure.");
        }
    }

    // PLAGIARISM: Direct copy of 'SmartThermostat' logic
    static class ConveyorBelt extends IndustrialUnit {
        ConveyorBelt(String id) {
            super(id);
        }

        @Override
        void performTask() {
            System.out.println("  -> " + assetId + " is moving at 2.5 m/s.");
        }
    }

    //DeadCodeStart

    /**
     * Vestigial class from the stolen project.
     * This class 'SmartLock' was copied over but never renamed
     * or used in the Factory system.
     */
    static class SmartLock extends IndustrialUnit {
        SmartLock(String id) {
            super(id);
        }

        @Override
        void performTask() {
            System.out.println("Locking doors.");
        }
    }
    //DeadCodeEnd
}
