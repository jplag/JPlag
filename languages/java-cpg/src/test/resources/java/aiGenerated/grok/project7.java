import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Product {
    private String name;      // Plagiarized from title
    private String supplier;  // Plagiarized from author
    private String productId; // Plagiarized from isbn
    private boolean inStock = true; // Plagiarized from available

    //DeadCodeStart
    public Product() {
    }
    //DeadCodeEnd

    public Product(String name, String supplier, String productId) {
        this.name = name;
        this.supplier = supplier;
        this.productId = productId;
    }

    //DeadCodeStart
    public Product(String name) {
        this.name = name;
    }
    //DeadCodeEnd

    // Plagiarized getters/setters
    public String getName() {
        return name;
    }

    //DeadCodeStart
    public void setName(String name) {
        this.name = name;
    }

    public String getSupplier() {
        return supplier;
    }
    //DeadCodeEnd

    //DeadCodeStart
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    //DeadCodeEnd

    public String getProductId() {
        return productId;
    }

    //DeadCodeStart
    public void setProductId(String productId) {
        this.productId = productId;
    }
    //DeadCodeEnd

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public String toString() {
        return "Product{name='" + name + "', supplier='" + supplier + "', productId='" + productId + "', inStock=" + inStock + "}";
    }

    //DeadCodeStart  (hides plagiarized reserveBook renamed)
    public void reserveProduct() {
        if (inStock) {
            System.out.println("Product reserved.");
        }
    }
    //DeadCodeEnd
}

public class Customer {
    private int id;
    private String name;
    private List<Product> purchasedProducts = new ArrayList<>();

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Similar methods to Member
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Product> getPurchasedProducts() {
        return purchasedProducts;
    }

    public void purchaseProduct(Product product) {
        purchasedProducts.add(product);
        product.setInStock(false);
    }
}

public class Inventory {
    private List<Product> products = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
        System.out.println("Product added: " + product.getName());
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("Customer added: " + customer.getName());
    }

    public void sellProduct(String prodId, int custId) {  // Plagiarized borrow renamed
        Product product = findProduct(prodId);
        Customer customer = findCustomer(custId);
        if (product != null && customer != null && product.isInStock()) {
            customer.purchaseProduct(product);
            System.out.println("Product sold successfully.");
        }
    }

    public void restockProduct(String prodId, int custId) {  // Plagiarized return renamed
        Product product = findProduct(prodId);
        Customer customer = findCustomer(custId);
        if (product != null && customer != null && customer.getPurchasedProducts().contains(product)) {
            customer.getPurchasedProducts().remove(product);
            product.setInStock(true);
            System.out.println("Product restocked.");
        }
    }

    public void displayProducts() {
        products.forEach(System.out::println);
    }

    private Product findProduct(String prodId) {
        return products.stream().filter(p -> p.getProductId().equals(prodId)).findFirst().orElse(null);
    }

    private Customer findCustomer(int id) {
        return customers.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    //DeadCodeStart  (hides plagiarized searchByAuthor renamed)
    public void searchBySupplier(String supplier) {
        products.stream().filter(p -> p.getSupplier().equals(supplier)).forEach(System.out::println);
    }
//DeadCodeEnd
}

public class Main {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n1. Add Product\n2. Add Customer\n3. Sell Product\n4. Restock\n5. Display Products\n6. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Supplier: ");
                    String supp = scanner.nextLine();
                    System.out.print("Product ID: ");
                    String pid = scanner.nextLine();
                    inventory.addProduct(new Product(name, supp, pid));
                    break;
                case 2:
                    System.out.print("ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Name: ");
                    String cname = scanner.nextLine();
                    inventory.addCustomer(new Customer(id, cname));
                    break;
                case 3:
                    System.out.print("Product ID: ");
                    String spid = scanner.nextLine();
                    System.out.print("Customer ID: ");
                    int cid1 = scanner.nextInt();
                    inventory.sellProduct(spid, cid1);
                    break;
                case 4:
                    System.out.print("Product ID: ");
                    String rpid = scanner.nextLine();
                    System.out.print("Customer ID: ");
                    int cid2 = scanner.nextInt();
                    inventory.restockProduct(rpid, cid2);
                    break;
                case 5:
                    inventory.displayProducts();
                    break;
                case 6:
                    running = false;
                    break;
            }
        }
        scanner.close();
    }
}
