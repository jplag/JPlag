import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * PROJECT F: Library Catalog
 * A standard system to manage book availability.
 */
public class LibraryCatalog {

    // Map: ISBN -> Is Available (true = in library, false = checked out)
    private static final Map<String, Boolean> catalog = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("--- City Library System ---");

        // Seed some data
        catalog.put("978-0134685991", true); // Effective Java
        catalog.put("978-0132350884", true); // Clean Code

        boolean active = true;
        while (active) {
            System.out.println("\n1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. List Available");
            System.out.println("4. Exit");
            System.out.print("Select: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    borrowBook(sc);
                    break;
                case "2":
                    returnBook(sc);
                    break;
                case "3":
                    listBooks();
                    break;
                case "4":
                    active = false;
                    break;
                case "99":
                    // Hidden admin menu that is never advertised and practically dead
                    System.out.println("Admin Mode: Resetting all books...");
                    for (String key : catalog.keySet()) {
                        catalog.put(key, true);
                    }
                    break;
                default:
                    System.out.println("Invalid.");
            }
        }
    }

    private static void borrowBook(Scanner sc) {
        System.out.print("Enter ISBN: ");
        String isbn = sc.nextLine();
        if (catalog.containsKey(isbn)) {
            if (catalog.get(isbn)) {
                catalog.put(isbn, false);
                System.out.println("Book borrowed successfully.");
            } else {
                System.out.println("Book is currently out.");
            }
        } else {
            System.out.println("Book not found in catalog.");
        }
    }

    private static void returnBook(Scanner sc) {
        System.out.print("Enter ISBN: ");
        String isbn = sc.nextLine();
        if (catalog.containsKey(isbn)) {
            catalog.put(isbn, true);
            System.out.println("Book returned.");
        } else {
            System.out.println("This book does not belong to our library.");
        }
    }

    private static void listBooks() {
        System.out.println("--- Available Books ---");
        for (Map.Entry<String, Boolean> entry : catalog.entrySet()) {
            if (entry.getValue()) {
                System.out.println("ISBN: " + entry.getKey());
            }
        }
    }

    /**
     * Deprecated Search Method.
     * Replaced by direct ISBN lookup in v2.0.
     * Kept for archival purposes but never called.
     */
    //DeadCodeStart
    private static void legacySearch(String title) {
        System.out.println("Searching legacy database for: " + title);
        if (title == null) return;

        // Simulating a slow search
        for (int i = 0; i < 1000; i++) {
            // busy wait
        }
        System.out.println("No results found in legacy DB.");
    }
    //DeadCodeEnd
}
