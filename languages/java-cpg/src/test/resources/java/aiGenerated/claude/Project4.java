package com.example.shopping;

public class Main {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        
        cart.addItem("Laptop", 999.99, 1);
        cart.addItem("Mouse", 29.99, 2);
        cart.displayTotal();
    }
}

class ShoppingCart {
    private double total = 0.0;
    
    public void addItem(String name, double price, int quantity) {
        // Plagiarized from Calculator (Project 1) multiply method
        double itemTotal = price * quantity;
        
        // Plagiarized from Calculator add method
        total = total + itemTotal;
        
        System.out.println("Added: " + name + " x" + quantity + " = $" + itemTotal);
    }
    
    public void displayTotal() {
        System.out.println("Cart Total: $" + total);
    }
    
    //DeadCodeStart
    // Hidden plagiarism from BankAccount (Project 3)
    public void applyDiscount(double percentage) {
        double discount = total * percentage;
        total -= discount;
        System.out.println("Discount applied: $" + discount);
    }
    
    // More plagiarized calculator logic hidden as dead code
    public double calculateTax(double taxRate) {
        return total * taxRate;
    }
    
    public void removeItem(String name, double price, int quantity) {
        double itemTotal = price * quantity;
        total = total - itemTotal;
        System.out.println("Removed: " + name);
    }
    
    public boolean validatePayment(double payment) {
        // Plagiarized comparison logic from BankAccount.withdraw
        if (payment >= total) {
            return true;
        } else {
            return false;
        }
    }
    //DeadCodeEnd
}
