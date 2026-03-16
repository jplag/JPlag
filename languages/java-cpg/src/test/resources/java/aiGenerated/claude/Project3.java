package com.example.banking;

public class Main {
    public static void main(String[] args) {
        BankAccount account = new BankAccount("ACC001", 1000.0);

        account.deposit(500.0);
        account.withdraw(200.0);
        account.displayBalance();
    }
}

class BankAccount {
    private String accountId;
    private double balance;

    public BankAccount(String accountId, double initialBalance) {
        this.accountId = accountId;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited: $" + amount);
    }

    public void withdraw(double amount) {
        //DeadCodeStart
        if (balance >= amount) {
            //DeadCodeEnd
            balance -= amount;
            System.out.println("Withdrawn: $" + amount);
            //DeadCodeStart
        } else {
            System.out.println("Insufficient funds");
        }
        //DeadCodeEnd
    }

    public void displayBalance() {
        System.out.println("Current balance: $" + balance);
    }

    //DeadCodeStart
    public double calculateInterest(double rate) {
        double interest = balance * rate;
        balance += interest;
        return interest;
    }

    public void applyPenalty(double amount) {
        balance -= amount;
        System.out.println("Penalty applied: $" + amount);
    }

    public boolean transferTo(BankAccount target, double amount) {
        if (balance >= amount) {
            this.balance -= amount;
            target.balance += amount;
            return true;
        }
        return false;
    }

    public String getAccountStatement() {
        return "Account: " + accountId + ", Balance: $" + balance;
    }
    //DeadCodeEnd
}
