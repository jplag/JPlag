import java.util.ArrayList;
import java.util.List;

/**
 * PROJECT R: Elevator Controller
 * CONCEPT: Event-Driven Programming with Interfaces.
 * Simulates a physical elevator moving between floors.
 */
public class ElevatorController {

    private int currentFloor = 0;
    private boolean doorsOpen = false;
    private List<ElevatorListener> listeners = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("--- Building A: Main Lift ---");
        ElevatorController lift = new ElevatorController();

        // Register an anonymous listener for events
        lift.addListener(new ElevatorListener() {
            @Override
            public void onFloorChanged(int newFloor) {
                System.out.println("[Display] " + newFloor);
            }

            @Override
            public void onDoors(boolean open) {
                System.out.println("[Mechanism] Doors " + open);
            }
        });

        lift.call(5);
        lift.call(2);

        //DeadCodeStart
        // Legacy Fire Service Mode
        // This logic overrides all calls and sends lift to ground
        boolean fireAlarm = false;
        if (fireAlarm) {
            System.out.println("FIRE DETECTED. DESCENDING.");
            lift.currentFloor = 0;
        }
        //DeadCodeEnd
    }

    public void addListener(ElevatorListener l) {
        listeners.add(l);
    }

    public void call(int targetFloor) {
        if (doorsOpen) closeDoors();

        System.out.println("Moving to " + targetFloor + "...");
        // Simulate movement
        while (currentFloor != targetFloor) {
            if (currentFloor < targetFloor) currentFloor++;
            else currentFloor--;

            notifyFloors();
        }
        openDoors();
    }

    private void openDoors() {
        this.doorsOpen = true;
        for (ElevatorListener l : listeners) l.onDoors(true);
    }

    private void closeDoors() {
        //DeadCodeStart
        // Safety Sensor Logic
        // Checks if an object is blocking the door
        int obstructionSensorVal = 0;
        if (obstructionSensorVal > 5) {
            System.out.println("Object detected. Re-opening.");
            return;
        }
        //DeadCodeEnd

        this.doorsOpen = false;
        for (ElevatorListener l : listeners) l.onDoors(false);
    }

    private void notifyFloors() {
        for (ElevatorListener l : listeners) l.onFloorChanged(currentFloor);
    }

    // --- Inner Interface ---
    interface ElevatorListener {
        void onFloorChanged(int newFloor);

        void onDoors(boolean open);
    }
}
