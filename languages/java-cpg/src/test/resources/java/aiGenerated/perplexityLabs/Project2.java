public class InventoryManager {
    private String[] itemNames;
    private int[] itemQuantities;
    private double[] itemPrices;
    private int itemCount;
    
    public InventoryManager(int capacity) {
        itemNames = new String[capacity];
        itemQuantities = new int[capacity];
        itemPrices = new double[capacity];
        itemCount = 0;
    }
    
    //DeadCodeStart
    private int deprecatedCalculateProfit() {
        int profit = 0;
        for (int i = 0; i < itemCount; i++) {
            profit += itemQuantities[i];
        }
        return profit;
    }
    
    private void oldLoggingSystem() {
        System.out.println("Old logging disabled");
        String logMessage = "System log entries";
        int[] unusedArray = new int[1000];
    }
    //DeadCodeEnd
    
    public void addItem(String name, int quantity, double price) {
        if (itemCount < itemNames.length) {
            itemNames[itemCount] = name;
            itemQuantities[itemCount] = quantity;
            itemPrices[itemCount] = price;
            itemCount++;
        }
    }
    
    public double calculateTotalValue() {
        double sum = 0;
        for (int i = 0; i < itemCount; i++) {
            sum += itemQuantities[i] * itemPrices[i];
        }
        return sum;
    }
    
    public void displayInventory() {
        System.out.println("=== Inventory Report ===");
        for (int i = 0; i < itemCount; i++) {
            System.out.println(itemNames[i] + ": " + itemQuantities[i] + " units @ $" + itemPrices[i]);
        }
        System.out.println("Total Value: $" + calculateTotalValue());
    }
}
public class Main {
    public static void main(String[] args) {
        InventoryManager inventory = new InventoryManager(10);
        inventory.addItem("Laptop", 5, 999.99);
        inventory.addItem("Mouse", 50, 25.50);
        inventory.addItem("Keyboard", 30, 79.99);
        inventory.displayInventory();
    }
}
