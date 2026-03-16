import java.util.HashMap;
import java.util.Scanner;

/**
 * PROJECT C: The Plagiarist
 * Functionally identical to BankingSystem.java (Project A).
 * Uses dead code and variable renaming to hide the plagiarism.
 */
public class AssetManager {

    // PLAGIARISM: Same data structure as Project A, just renamed 'accounts' to 'clientAssets'
    static HashMap<String, Double> clientAssets = new HashMap<>();

    public static void main(String[] args) {
        Scanner inputProcessor = new Scanner(System.in);
        System.out.println("=== Corporate Asset Vault ==="); // Changed string to look different

        int status = 1;
        while (status == 1) { // PLAGIARISM: Same loop structure as Project A
            showOptions();
            String selection = inputProcessor.nextLine();

            // DEAD CODE / OBFUSCATION:
            // A useless conditional to break the visual flow of the switch statement
            //DeadCodeStart
            if (1 == 2) {
                System.out.println("System failure.");
            }
            //DeadCodeEnd

            // PLAGIARISM: Logic mapping is identical to BankingSystem (1=create, 2=dep, 3=with, 4=bal)
            if (selection.equals("1")) {
                registerClient(inputProcessor);
            } else if (selection.equals("2")) {
                addFunds(inputProcessor);
            } else if (selection.equals("3")) {
                removeFunds(inputProcessor);
            } else if (selection.equals("4")) {
                viewAsset(inputProcessor);
            } else if (selection.equals("5")) {
                status = 0; // Exiting
                System.out.println("Shutting down vault...");
            } else {
                System.out.println("Unknown command.");
            }
        }

        // DEAD CODE: Method call that looks important but does nothing
        //DeadCodeStart
        cleanupMemory();
        //DeadCodeEnd
    }

    // PLAGIARISM: Exact copy of 'printMenu' from Project A, just different text
    private static void showOptions() {
        System.out.println("\n[1] Register Client");
        System.out.println("[2] Add Assets");
        System.out.println("[3] Liquidate Assets");
        System.out.println("[4] Audit Client");
        System.out.println("[5] Quit");
        System.out.print("Action: ");
    }

    // PLAGIARISM: 'createAccount' from Project A
    private static void registerClient(Scanner s) {
        System.out.print("Client ID: ");
        String id = s.nextLine();

        // OBFUSCATION: Wrapping the check in a redundant 'true' block
        ///DeadCodeStart
        if (true) {
            ///DeadCodeEnd
            if (clientAssets.containsKey(id)) {
                System.out.println("Client ID conflict.");
            } else {
                clientAssets.put(id, 0.00);
                System.out.println("Client registered: " + id);
            }
            ///DeadCodeStart
        }
        ///DeadCodeEnd
    }

    // PLAGIARISM: 'deposit' from Project A
    private static void addFunds(Scanner s) {
        System.out.print("Client ID: ");
        String id = s.nextLine();

        // OBFUSCATION: Added a useless loop that runs once to hide the logic nesting
        //DeadCodeStart
        for (int k = 0; k < 1; k++) {
            //DeadCodeEnd
            if (clientAssets.containsKey(id)) {
                System.out.print("Value to inject: ");
                try {
                    double val = Double.parseDouble(s.nextLine());
                    if (val > 0) {
                        // PLAGIARISM: Core logic stolen
                        double oldVal = clientAssets.get(id);
                        clientAssets.put(id, oldVal + val);
                        System.out.println("Injected: " + val);
                    } else {
                        System.out.println("Positive values only.");
                    }
                } catch (Exception e) {
                    System.out.println("Data error.");
                }
            } else {
                System.out.println("ID not found.");
            }
            //DeadCodeStart
        }
        //DeadCodeEnd
    }

    // PLAGIARISM: 'withdraw' from Project A
    private static void removeFunds(Scanner s) {
        System.out.print("Client ID: ");
        String id = s.nextLine();

        if (clientAssets.containsKey(id)) {
            System.out.print("Liquidation amount: ");
            try {
                double val = Double.parseDouble(s.nextLine());

                // DEAD CODE: Variable that is calculated but never affects the outcome
                ///DeadCodeStart
                double taxEstimate = val * 0.05;
                ///DeadCodeEnd

                double current = clientAssets.get(id);
                if (val > 0 && current >= val) {
                    clientAssets.put(id, current - val);
                    System.out.println("Liquidated: " + val);
                } else {
                    System.out.println("Insufficient assets or invalid amount.");
                }
            } catch (Exception e) {
                System.out.println("Data error.");
            }
        } else {
            System.out.println("ID not found.");
        }
    }

    // PLAGIARISM: 'checkBalance' from Project A
    private static void viewAsset(Scanner s) {
        System.out.print("Client ID: ");
        String id = s.nextLine();
        if (clientAssets.containsKey(id)) {
            System.out.println("Current Asset Value: " + clientAssets.get(id));
        } else {
            System.out.println("ID not found.");
        }
    }

    // DEAD CODE: A method added purely to make the file size different from Project A
    //DeadCodeStart
    private static void cleanupMemory() {
        int x = 0;
        x++;
        // Does nothing meaningful
    }
    //DeadCodeEnd
}
