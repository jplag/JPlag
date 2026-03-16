import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * PROJECT O: Corporate Payroll
 * PLAGIARISM: Steals the interface/abstract class structure from RPGBattleSystem.
 * - 'Combatant' becomes 'Payable'
 * - 'Character' becomes 'Employee'
 * - 'Paladin' becomes 'Executive' (High Salary)
 * - 'Necromancer' becomes 'Contractor' (Variable Pay)
 * * DEAD CODE: Checks for "Mana" and "HP" inside the payroll logic.
 */
public class CorporatePayroll {

    public static void main(String[] args) {
        System.out.println("--- HR Payroll System 2024 ---");
        
        // PLAGIARISM: Same List<Interface> structure
        List<Payable> staff = new ArrayList<>();
        staff.add(new Executive("CEO Smith", 100000));
        staff.add(new Contractor("Dev Jones", 50)); // Hourly rate

        // Simulate pay cycles (was 'Rounds')
        for (int i = 1; i <= 3; i++) {
            System.out.println("\nMonth " + i);
            for (Payable p : staff) {
                if (p.isActive()) { // Was 'isAlive'
                    p.processPayment(); // Was 'performAction'
                }
            }
        }

        //DeadCodeStart
        // PLAGIARISM GHOST: 
        // This is the 'dropLoot' logic from the RPG. 
        // Why would a payroll system have a "Sword" or "Shield"?
        boolean bonusDrop = false;
        if (bonusDrop) {
            String[] items = {"Sword of Layoffs", "Shield of Tax Evasion"};
            System.out.println("Looted: " + items[0]);
        }
        //DeadCodeEnd
    }

    // --- Interfaces & Hierarchy (Stolen) ---

    interface Payable {
        void processPayment(); // Was 'performAction'
        boolean isActive();    // Was 'isAlive'
        void deductTax(int amount); // Was 'takeDamage'
    }

    // PLAGIARISM: Direct copy of 'Character' abstract class
    abstract static class Employee implements Payable {
        String name;
        int balance; // Was 'hp'
        Random rng = new Random();

        Employee(String n, int b) { this.name = n; this.balance = b; }

        public boolean isActive() { return balance > 0; } // Logic is identical
        
        public void deductTax(int amount) {
            this.balance -= amount;
            System.out.println(name + " pays " + amount + " tax. Balance: " + balance);
        }
    }

    // PLAGIARISM: 'Paladin' Logic (Steady, high numbers)
    static class Executive extends Employee {
        Executive(String n, int b) { super(n, b); }

        @Override
        public void processPayment() {
            // Executive logic: High Bonus
            int pay = rng.nextInt(1500) + 5000;
            System.out.println(name + " authorizes bonus of $" + pay);
            
            //DeadCodeStart
            // GHOST CODE: The 'Holy Light' healing logic
            // Checks for a 'mana' variable that doesn't exist in this scope anymore
            if (false) {
                // System.out.println("Casting Healing Spell..."); 
                // this.mana -= 10;
            }
            //DeadCodeEnd
        }
    }

    // PLAGIARISM: 'Necromancer' Logic (High variance)
    static class Contractor extends Employee {
        Contractor(String n, int b) { super(n, b); }

        @Override
        public void processPayment() {
            // Contractor logic: Variable hours
            int pay = rng.nextInt(2500);
            System.out.println(name + " bills hours for $" + pay);
            
            //DeadCodeStart
            // Vestigial logic checking for magic resistance
            int magicResist = 0;
            if (magicResist > 10) {
                System.out.println("Spell resisted!");
            }
            //DeadCodeEnd
        }
    }
}
