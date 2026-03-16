import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse("Main Warehouse");

        // Create products
        Product product1 = new Product("Laptop", "Manufacturer A", "SKU-001", 2020);
        Product product2 = new Product("Monitor", "Manufacturer B", "SKU-002", 2021);
        Product product3 = new Product("Keyboard", "Manufacturer C", "SKU-003", 2019);

        // Create customers
        Customer customer1 = new Customer("Alice Johnson", "C001", "alice@company.com");
        Customer customer2 = new Customer("Bob Williams", "C002", "bob@company.com");

        // Add to warehouse
        warehouse.addProduct(product1);
        warehouse.addProduct(product2);
        warehouse.addProduct(product3);
        warehouse.registerCustomer(customer1);
        warehouse.registerCustomer(customer2);

        // Perform operations
        System.out.println("=== Inventory System ===");
        warehouse.displayAvailableProducts();

        System.out.println("\n--- Processing orders ---");
        warehouse.processOrder(customer1, product1);
        warehouse.processOrder(customer2, product2);

        System.out.println("\n--- Available products after orders ---");
        warehouse.displayAvailableProducts();

        System.out.println("\n--- Customer order history ---");
        customer1.displayOrderHistory();

        //DeadCodeStart
        // PLAGIARIZED: Similar pattern from Library's Main class
        InventoryAnalytics analytics = new InventoryAnalytics(warehouse);
        analytics.calculateDemandScore(product1);
        //DeadCodeEnd

        // Additional dead code to obscure plagiarism
        System.out.println("\n--- Checking warehouse capacity ---");
        //DeadCodeStart
        int capacity = warehouse.getTotalCapacity();
        // capacity is calculated but never used
        //DeadCodeEnd
    }
}

// Product.java
class Product {
    private String name;
    private String manufacturer;
    private String sku;
    private int manufactureYear;
    private boolean inStock;
    private int orderCount;

    // PLAGIARIZED: Constructor pattern directly from Book class
    public Product(String name, String manufacturer, String sku, int manufactureYear) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.sku = sku;
        this.manufactureYear = manufactureYear;
        this.inStock = true;
        this.orderCount = 0;
    }

    public String getName() {
        return name;
    }

    //DeadCodeStart
    public String getManufacturer() {
        return manufacturer;
    }

    public String getSku() {
        return sku;
    }
    //DeadCodeEnd

    // PLAGIARIZED: Same logic as Book.isAvailable()
    public boolean inStock() {
        return inStock;
    }

    // PLAGIARIZED: Same as Book.setAvailable()
    public void setInStock(boolean stock) {
        inStock = stock;
    }

    // PLAGIARIZED: Exact copy of Book.incrementBorrowCount()
    public void incrementOrderCount() {
        orderCount++;
    }

    //DeadCodeStart
    public int getOrderCount() {
        return orderCount;
    }

    public int getManufactureYear() {
        return manufactureYear;
    }

    // PLAGIARIZED from Book.updateTitle() - hidden in dead code
    public void updateName(String newName) {
        // Never used - dead code
        this.name = newName;
        System.out.println("Name updated to: " + newName);
    }

    // PLAGIARIZED: Same logic as Book.isClassic()
    private boolean isLegacyProduct() {
        // Unused helper method - plagiarized and hidden
        return manufactureYear < 1950;
    }

    // PLAGIARIZED from Book.getGenre()
    public String getCategory() {
        // Returns hardcoded value, never called
        return "Electronics";
    }

    public double calculateDepreciation() {
        // Additional dead code
        int age = 2024 - manufactureYear;
        return age * 0.1;
    }
    //DeadCodeEnd

    // PLAGIARIZED: toString format similar to Book
    @Override
    public String toString() {
        return String.format("'%s' by %s [%s] - %s", name, manufacturer, sku, "In Stock");
    }
}

// Customer.java

class Customer {
    private String name;
    private String customerId;
    private String email;
    private List<Product> orderedProducts;
    private List<String> orderHistory;
    private int totalOrders;

    // PLAGIARIZED: Exact same structure as Member constructor
    public Customer(String name, String customerId, String email) {
        this.name = name;
        this.customerId = customerId;
        this.email = email;
        this.orderedProducts = new ArrayList<>();
        this.orderHistory = new ArrayList<>();
        this.totalOrders = 0;
    }

    public String getName() {
        return name;
    }

    //DeadCodeStart
    public String getCustomerId() {
        return customerId;
    }

    public List<Product> getOrderedProducts() {
        return orderedProducts;
    }
    //DeadCodeEnd

    // PLAGIARIZED: Same logic as Member.borrowBook()
    public void orderProduct(Product product) {
        orderedProducts.add(product);
        orderHistory.add(product.getName());
        totalOrders++;
    }

    //DeadCodeStart
    // PLAGIARIZED: Copy of Member.returnBook()
    public void cancelOrder(Product product) {
        orderedProducts.remove(product);
    }
    //DeadCodeEnd

    // PLAGIARIZED: Near-exact copy of Member.displayBorrowingHistory()
    public void displayOrderHistory() {
        System.out.println("Order history for " + name + ":");
        for (String productName : orderHistory) {
            System.out.println("  - " + productName);
        }
    }

    //DeadCodeStart
    public String getEmail() {
        return email;
    }

    // PLAGIARIZED from Member.updateEmail() - in dead code
    public void updateEmail(String newEmail) {
        // Never called
        this.email = newEmail;
        System.out.println("Email updated for " + name);
    }

    // PLAGIARIZED: Exact same logic as Member.calculateMembershipLevel()
    public int calculateCustomerTier() {
        // Complex dead code that's never used - plagiarized
        if (totalOrders > 50) return 3;
        else if (totalOrders > 20) return 2;
        else return 1;
    }

    // PLAGIARIZED from Member.canBorrowMore()
    private boolean canOrderMore() {
        // Unused validation method - plagiarized logic
        return orderedProducts.size() < 5;
    }

    // PLAGIARIZED from Member.getRecommendations()
    public List<String> getSuggestedProducts() {
        // Dead code that returns dummy data
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Suggested Product 1");
        suggestions.add("Suggested Product 2");
        return suggestions;
    }

    public double calculateLifetimeValue() {
        // Extra dead code
        return totalOrders * 150.0;
    }
    //DeadCodeEnd
}

// Warehouse.java

class Warehouse {
    private String name;
    private List<Product> products;
    private List<Customer> customers;

    public Warehouse(String name) {
        this.name = name;
        this.products = new ArrayList<>();
        this.customers = new ArrayList<>();
    }

    // PLAGIARIZED: Exact copy of Library.addBook()
    public void addProduct(Product product) {
        products.add(product);
        System.out.println("Added product: " + product.getName());
    }

    // PLAGIARIZED: Copy of Library.registerMember()
    public void registerCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("Registered customer: " + customer.getName());
    }

    // PLAGIARIZED: Same logic structure as Library.borrowBook()
    public void processOrder(Customer customer, Product product) {
        if (product.inStock()) {
            product.setInStock(false);
            product.incrementOrderCount();
            customer.orderProduct(product);
            System.out.println(customer.getName() + " ordered " + product.getName());
        } else {
            System.out.println("Product is not in stock: " + product.getName());
        }
    }

    //DeadCodeStart
    // PLAGIARIZED: Copy of Library.returnBook()
    public void restockProduct(Customer customer, Product product) {
        product.setInStock(true);
        customer.cancelOrder(product);
        System.out.println(customer.getName() + " cancelled order for " + product.getName());
    }
    //DeadCodeEnd

    // PLAGIARIZED: Near-identical to Library.displayAvailableBooks()
    public void displayAvailableProducts() {
        System.out.println("Available products in " + name + ":");
        for (Product product : products) {
            if (product.inStock()) {
                System.out.println("  " + product);
            }
        }
    }

    //DeadCodeStart
    public List<Product> getProducts() {
        return products;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    // PLAGIARIZED from Library.findBookByIsbn() - hidden in dead code
    public Product findProductBySku(String sku) {
        // Never used search functionality - plagiarized
        for (Product product : products) {
            if (product.getSku().equals(sku)) {
                return product;
            }
        }
        return null;
    }

    // PLAGIARIZED from Library.findMemberById()
    public Customer findCustomerById(String customerId) {
        // Dead code for finding customers - plagiarized
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }

    // PLAGIARIZED from Library.generateMonthlyReport()
    private void generateInventoryReport() {
        // Incomplete dead code - plagiarized structure
        System.out.println("Generating inventory report for " + name);
        int totalProducts = products.size();
        int availableProducts = 0;
        for (Product product : products) {
            if (product.inStock()) availableProducts++;
        }
    }

    // PLAGIARIZED from Library.removeBook()
    public void removeProduct(String sku) {
        // Never called removal method - plagiarized
        Product toRemove = findProductBySku(sku);
        if (toRemove != null) {
            products.remove(toRemove);
            System.out.println("Removed product: " + toRemove.getName());
        }
    }

    public int getTotalCapacity() {
        // Dead code that calculates but result is never used
        return products.size() * 100;
    }

    private void optimizeStorage() {
        // Dead code with complex logic
        List<Product> inStockProducts = new ArrayList<>();
        for (Product p : products) {
            if (p.inStock()) {
                inStockProducts.add(p);
            }
        }
        // Do nothing with the list
    }
    //DeadCodeEnd
}

// InventoryAnalytics.java
//DeadCodeStart
// PLAGIARIZED: Entire class structure copied from LibraryAnalytics
class InventoryAnalytics {
    private Warehouse warehouse;

    public InventoryAnalytics(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    // PLAGIARIZED from LibraryAnalytics.calculatePopularityScore()
    public double calculateDemandScore(Product product) {
        // Entire class is dead code - never actually used, but plagiarized
        return product.getOrderCount() * 1.5;
    }

    // PLAGIARIZED from LibraryAnalytics.getMostPopularBooks()
    public List<Product> getMostOrderedProducts(int count) {
        List<Product> allProducts = warehouse.getProducts();
        // Incomplete sorting logic - plagiarized
        return allProducts.subList(0, Math.min(count, allProducts.size()));
    }

    // PLAGIARIZED from LibraryAnalytics.printStatistics()
    public void printStatistics() {
        System.out.println("Warehouse Statistics:");
        System.out.println("Total products: " + warehouse.getProducts().size());
    }

    public void forecastDemand() {
        // Additional dead code method
        System.out.println("Forecasting future demand...");
    }
}
//DeadCodeEnd
