import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * PROJECT G: Inventory Tracker
 * PLAGIARISM: Steals logic from LibraryCatalog.java.
 * - 'catalog' becomes 'inventory'
 * - 'borrowBook' becomes 'checkoutItem'
 * - 'returnBook' becomes 'restockItem'
 * * DEAD CODE: Contains "Ghost" code - leftovers from the plagiarism 
 * that were commented out or gated behind false flags.
 */
public class InventoryTracker {

    // PLAGIARISM: Same structure as LibraryCatalog
    private static final Map<String, Boolean> inventory = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Warehouse Inventory 1.0 ---");

        inventory.put("SKU-001", true); 
        inventory.put("SKU-002", true);

        //DeadCodeStart
        // This variable is a leftover from the copy-paste job
        // It is defined but never used in the active logic.
        boolean libraryIsOpen = true; 
        if (!libraryIsOpen) {
            System.out.println("Library is closed."); // The word "Library" reveals the theft
        }
        //DeadCodeEnd

        boolean systemRunning = true;
        while (systemRunning) {
            System.out.println("\n1. Checkout Item");
            System.out.println("2. Restock Item");
            System.out.println("3. List Stock");
            System.out.println("4. Shutdown");
            System.out.print("Command: ");

            String input = scanner.nextLine();

            // PLAGIARISM: Same switch logic as LibraryCatalog
            if (input.equals("1")) {
                checkoutItem(scanner);
            } else if (input.equals("2")) {
                restockItem(scanner);
            } else if (input.equals("3")) {
                listStock();
            } else if (input.equals("4")) {
                systemRunning = false;
            } else {
                System.out.println("Unknown command.");
            }

            //DeadCodeStart
            if (false) {
                 // Vestigial logic from the original file
                 // attempting to call methods that don't exist in this version
                 // checkOverdueBooks(); 
            }
            //DeadCodeEnd
        }
    }

    private static void checkoutItem(Scanner s) {
        System.out.print("Enter SKU: ");
        String sku = s.nextLine();
        
        //DeadCodeStart
        // Useless check to obfuscate the stolen logic below
        if (sku.equals("MAGIC_STRING")) {
            return;
        }
        //DeadCodeEnd

        if (inventory.containsKey(sku)) {
            if (inventory.get(sku)) {
                inventory.put(sku, false);
                System.out.println("Item checked out.");
            } else {
                System.out.println("Item out of stock.");
            }
        } else {
            System.out.println("SKU not found.");
        }
    }

    private static void restockItem(Scanner s) {
        System.out.print("Enter SKU: ");
        String sku = s.nextLine();
        if (inventory.containsKey(sku)) {
            inventory.put(sku, true);
            System.out.println("Item restocked.");
        } else {
            System.out.println("Item not part of inventory.");
        }
    }

    private static void listStock() {
        System.out.println("--- Current Stock ---");
        for (Map.Entry<String, Boolean> entry : inventory.entrySet()) {
            if (entry.getValue()) {
                System.out.println("SKU: " + entry.getKey());
            }
        }
    }

    //DeadCodeStart
    /**
     * UNUSED METHOD
     * This was copied accidentally from the Library project 
     * but the method body was emptied to hide the evidence.
     */
    private static void calculateLateFees() {
        // TODO: Remove this method before production
        double fee = 0.0;
        int daysLate = 5;
        fee = daysLate * 0.50; // Logic executed but result is void
    }
    //DeadCodeEnd
}
