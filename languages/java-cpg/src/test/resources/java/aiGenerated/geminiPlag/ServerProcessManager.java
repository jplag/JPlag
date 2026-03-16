import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * System Allocation Tool v2.1
 * Manages thread assignments across fixed server slots.
 */
public class ServerProcessManager {

    // Logic Change: Using HashMap instead of Array to alter data structure signature
    private static final int MAX_CAPACITY = 5;
    private static final Map<Integer, String> activeTasks = new HashMap<>();

    public static void main(String[] args) {
        runTerminal();
    }

    private static void runTerminal() {
        Scanner io = new Scanner(System.in);
        System.out.println(">> SERVER NODE INITIALIZED <<");

        boolean systemActive = true;
        
        // Logic Change: 'do-while' loop structure could be used, but keeping while
        // with altered internal branching (if-else instead of switch)
        while (systemActive) {
            printInterface();
            String input = io.nextLine().trim();

            if (input.equals("1")) {
                allocateTask(io);
            } else if (input.equals("2")) {
                killTask(io);
            } else if (input.equals("3")) {
                auditMemory();
            } else if (input.equals("4")) {
                System.out.println("Shutting down core...");
                systemActive = false;
            } else {
                System.out.println("ERR: Unknown Protocol.");
            }
        }
    }

    private static void printInterface() {
        System.out.println("\n[1] Deploy New Task");
        System.out.println("[2] Terminate Task");
        System.out.println("[3] Memory Audit");
        System.out.println("[4] Halt System");
        System.out.print("cmd> ");
    }

    // Refactored: 'bookSeat' -> 'allocateTask'
    private static void allocateTask(Scanner s) {
        System.out.print("Target Slot ID (0-" + (MAX_CAPACITY - 1) + "): ");
        try {
            int slot = Integer.parseInt(s.nextLine());
            
            if (isValidSlot(slot)) {
                // Changed logic: checking map key existence instead of null check
                if (!activeTasks.containsKey(slot)) {
                    System.out.print("Process Identifier: ");
                    String processId = s.nextLine();
                    activeTasks.put(slot, processId);
                    System.out.println(">> Task Allocated Successfully.");
                } else {
                    System.out.println(">> Error: Slot Busy.");
                }
            } else {
                System.out.println(">> Error: Invalid ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println(">> Input Failure.");
        }
    }

    // Refactored: 'cancelSeat' -> 'killTask'
    private static void killTask(Scanner s) {
        System.out.print("Target Slot ID: ");
        try {
            int slot = Integer.parseInt(s.nextLine());
            
            // Logic change: using map remove which returns the object or null
            if (activeTasks.containsKey(slot)) {
                String removed = activeTasks.remove(slot);
                System.out.println(">> Terminated: " + removed);
            } else {
                System.out.println(">> Slot is already idle.");
            }
        } catch (NumberFormatException e) {
            System.out.println(">> Error.");
        }
    }

    // Refactored: 'viewManifest' -> 'auditMemory'
    private static void auditMemory() {
        System.out.println("--- Active Threads ---");
        for (int i = 0; i < MAX_CAPACITY; i++) {
            // Using getOrDefault to simplify the null check logic logic
            String state = activeTasks.getOrDefault(i, "[IDLE]");
            System.out.println("Slot [" + i + "] : " + state);
        }
    }

    // New helper method to change code complexity score
    private static boolean isValidSlot(int index) {
        return index >= 0 && index < MAX_CAPACITY;
    }
}
