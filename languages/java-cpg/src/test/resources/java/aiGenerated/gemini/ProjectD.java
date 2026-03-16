import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * PROJECT D: The Hider
 * Functionally, this is a Math Tutor app.
 * However, it uses Dead Code to hide a plagiarized version of the BankingSystem.
 */
public class MathTutor {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random rand = new Random();
        System.out.println("--- Math Tutor 1.0 ---");

        int correct = 0;
        for (int i = 0; i < 3; i++) {
            int a = rand.nextInt(10);
            int b = rand.nextInt(10);
            System.out.print("What is " + a + " + " + b + "? ");
            try {
                int ans = Integer.parseInt(scanner.nextLine());
                if (ans == (a + b)) {
                    System.out.println("Correct!");
                    correct++;
                } else {
                    System.out.println("Wrong.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        System.out.println("Score: " + correct + "/3");

        // DEAD CODE CALL
        // This method is called, but the logic inside is gated by 'if (false)'
        // effectively hiding the plagiarized code within the executable.
        //DeadCodeStart
        executeHiddenLogic();
        //DeadCodeEnd
    }

    private static void executeHiddenLogic() {
        // DEAD CODE & PLAGIARISM COMBO
        // The compiler may ignore this, but it exists in the source code.
        // It is a direct copy of BankingSystem.java logic, hidden here.
        ///DeadCodeStart
        if (false) {
            Map<String, Double> hiddenAccounts = new HashMap<>();
            Scanner sc = new Scanner(System.in);
            boolean running = true;

            // Stolen Banking Logic
            while (running) {
                System.out.println("1. Create (Stolen)");
                System.out.println("2. Deposit (Stolen)");
                String cmd = sc.nextLine();

                if (cmd.equals("1")) {
                    String name = sc.nextLine();
                    hiddenAccounts.put(name, 0.0);
                } else if (cmd.equals("2")) {
                    String name = sc.nextLine();
                    double amount = Double.parseDouble(sc.nextLine());
                    hiddenAccounts.put(name, hiddenAccounts.get(name) + amount);
                }
            }
        }
        ///DeadCodeEnd
    }
}
