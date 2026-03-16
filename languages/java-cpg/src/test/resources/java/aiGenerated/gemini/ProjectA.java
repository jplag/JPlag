import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * PROJECT A: The Original
 * A simple banking system to manage accounts.
 * Contains minor dead code (debug flag).
 */
public class BankingSystem {

    private static final Map<String, Double> accounts = new HashMap<>();
    private static final boolean DEBUG_MODE = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Simple Banking System 1.0 ---");

        boolean running = true;
        while (running) {
            printMenu();
            String command = scanner.nextLine();

//            switch (command) {        //Does not work with java-cpg graph normalization
//                case "1":
//                    createAccount(scanner);
//                    break;
//                case "2":
//                    deposit(scanner);
//                    break;
//                case "3":
//                    withdraw(scanner);
//                    break;
//                case "4":
//                    checkBalance(scanner);
//                    break;
//                case "5":
//                    running = false;
//                    System.out.println("Exiting...");
//                    break;
//                default:
//                    System.out.println("Invalid option.");
//            }

            if (command.equals("1")) {
                createAccount(scanner);
            } else if (command.equals("2")) {
                deposit(scanner);
            } else if (command.equals("3")) {
                withdraw(scanner);
            } else if (command.equals("4")) {
                checkBalance(scanner);
            } else if (command.equals("5")) {
                running = false;
                System.out.println("Exiting...");
            } else {
                System.out.println("Invalid option.");
            }


            //DeadCodeStart
            if (DEBUG_MODE) {
                System.out.println("[DEBUG] Current memory usage: " + Runtime.getRuntime().totalMemory());
            }
            //DeadCodeEnd
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n1. Create Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Check Balance");
        System.out.println("5. Exit");
        System.out.print("Choose: ");
    }

    private static void createAccount(Scanner sc) {
        System.out.print("Enter account holder name: ");
        String name = sc.nextLine();
        if (accounts.containsKey(name)) {
            System.out.println("Account already exists.");
        } else {
            accounts.put(name, 0.0);
            System.out.println("Account created for " + name);
        }
    }

    private static void deposit(Scanner sc) {
        System.out.print("Enter name: ");
        String name = sc.nextLine();
        if (accounts.containsKey(name)) {
            System.out.print("Amount to deposit: ");
            try {
                double amount = Double.parseDouble(sc.nextLine());
                if (amount > 0) {
                    accounts.put(name, accounts.get(name) + amount);
                    System.out.println("Deposited " + amount);
                } else {
                    System.out.println("Amount must be positive.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void withdraw(Scanner sc) {
        System.out.print("Enter name: ");
        String name = sc.nextLine();
        if (accounts.containsKey(name)) {
            System.out.print("Amount to withdraw: ");
            try {
                double amount = Double.parseDouble(sc.nextLine());
                double current = accounts.get(name);
                if (amount > 0 && current >= amount) {
                    accounts.put(name, current - amount);
                    System.out.println("Withdrawn " + amount);
                } else {
                    System.out.println("Invalid amount or insufficient funds.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void checkBalance(Scanner sc) {
        System.out.print("Enter name: ");
        String name = sc.nextLine();
        if (accounts.containsKey(name)) {
            System.out.println("Balance: " + accounts.get(name));
        } else {
            System.out.println("Account not found.");
        }
    }

    //DeadCodeStart
    private static void transfer(String from, String to, double amount) {
        // TODO: Implement transfer logic
        System.out.println("Transfer feature coming soon.");
    }
    //DeadCodeEnd

}
