import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Account {
    private String accountNumber;
    private String owner;
    private double balance;
    //DeadCodeStart
    private boolean active = true;
    //DeadCodeEnd

    public Account(String accountNumber, String owner, double balance) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = balance;
    }

    // Getters/setters
    public String getAccountNumber() {
        return accountNumber;
    }

    //DeadCodeStart
    public String getOwner() {
        return owner;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return active;
    }
    //DeadCodeEnd

    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Account{number='" + accountNumber + "', owner='" + owner + "', balance=" + balance + "}";
    }
}

public class Bank {
    private List<Account> accounts = new ArrayList<>();

    public void addAccount(Account account) {
        accounts.add(account);
        System.out.println("Account added: " + account.getAccountNumber());
    }

    public void deposit(String accNum, double amount) {
        Account acc = findAccount(accNum);
        if (acc != null) {
            acc.deposit(amount);
            System.out.println("Deposited: " + amount);
        }
    }

    public void withdraw(String accNum, double amount) {
        Account acc = findAccount(accNum);
        if (acc != null && acc.withdraw(amount)) {
            System.out.println("Withdrawn: " + amount);
        }
    }

    public void displayAccounts() {
        accounts.forEach(System.out::println);
    }

    private Account findAccount(String accNum) {
        return accounts.stream().filter(a -> a.getAccountNumber().equals(accNum)).findFirst().orElse(null);
    }

    //DeadCodeStart
    // Plagiarized from Member/Customer borrow/purchase renamed and unused
    public void transferFunds(String fromAcc, String toAcc, double amount) {
        Account from = findAccount(fromAcc);
        Account to = findAccount(toAcc);
        if (from != null && to != null && from.withdraw(amount)) {
            to.deposit(amount);
        }
    }

    public void auditAccounts() {
        accounts.forEach(a -> System.out.println("Audit: " + a.getBalance()));
    }
//DeadCodeEnd
}

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n1. Add Account\n2. Deposit\n3. Withdraw\n4. Display Accounts\n5. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Account Number: ");
                    String num = scanner.nextLine();
                    System.out.print("Owner: ");
                    String own = scanner.nextLine();
                    System.out.print("Initial Balance: ");
                    double bal = scanner.nextDouble();
                    bank.addAccount(new Account(num, own, bal));
                    break;
                case 2:
                    System.out.print("Account Number: ");
                    String dnum = scanner.nextLine();
                    System.out.print("Amount: ");
                    double damt = scanner.nextDouble();
                    bank.deposit(dnum, damt);
                    break;
                case 3:
                    System.out.print("Account Number: ");
                    String wnum = scanner.nextLine();
                    System.out.print("Amount: ");
                    double wamt = scanner.nextDouble();
                    bank.withdraw(wnum, wamt);
                    break;
                case 4:
                    bank.displayAccounts();
                    break;
                case 5:
                    running = false;
                    break;
            }
        }
        scanner.close();
    }
}
