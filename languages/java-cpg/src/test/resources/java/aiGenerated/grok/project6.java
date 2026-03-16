import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private boolean available = true;

    //DeadCodeStart
    public Book() {
    }
    //DeadCodeEnd

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    //DeadCodeStart
    public Book(String title) {
        this.title = title;
    }
    //DeadCodeEnd

    // Getters and setters
    public String getTitle() {
        return title;
    }

    //DeadCodeStart
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    //DeadCodeEnd

    public String getIsbn() {
        return isbn;
    }

    //DeadCodeStart
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    //DeadCodeEnd

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public String toString() {
        return "Book{title='" + title + "', author='" + author + "', isbn='" + isbn + "', available=" + available + "}";
    }

    //DeadCodeStart
    public void reserveBook() {
        if (available) {
            System.out.println("Book reserved.");
        }
    }
//DeadCodeEnd
}

public class Member {
    private int id;
    private String name;
    private List<Book> borrowedBooks = new ArrayList<>();

    public Member(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    //DeadCodeStart
    public void setId(int id) {
        this.id = id;
    }
    //DeadCodeEnd

    public String getName() {
        return name;
    }

    //DeadCodeStart
    public void setName(String name) {
        this.name = name;
    }


    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }
    //DeadCodeEnd

    public void borrowBook(Book book) {
        borrowedBooks.add(book);
        book.setAvailable(false);
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
        book.setAvailable(true);
    }

    @Override
    public String toString() {
        return "Member{id=" + id + ", name='" + name + "', borrowedBooks.size=" + borrowedBooks.size() + "}";
    }
}

public class Library {
    private List<Book> books = new ArrayList<>();
    private List<Member> members = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
        System.out.println("Book added: " + book.getTitle());
    }

    public void addMember(Member member) {
        members.add(member);
        System.out.println("Member added: " + member.getName());
    }

    public void borrowBook(String isbn, int memberId) {
        Book book = findBook(isbn);
        Member member = findMember(memberId);
        if (book != null && member != null && book.isAvailable()) {
            member.borrowBook(book);
            System.out.println("Book borrowed successfully.");
        } else {
            System.out.println("Cannot borrow book.");
        }
    }

    public void returnBook(String isbn, int memberId) {
        Book book = findBook(isbn);
        Member member = findMember(memberId);
        if (book != null && member != null) {
            member.returnBook(book);
            System.out.println("Book returned successfully.");
        }
    }

    public void displayBooks() {
        books.forEach(System.out::println);
    }

    public void displayMembers() {
        members.forEach(System.out::println);
    }

    private Book findBook(String isbn) {
        return books.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().orElse(null);
    }

    private Member findMember(int id) {
        return members.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
    }

    //DeadCodeStart
    public void searchByAuthor(String author) {
        books.stream().filter(b -> b.getAuthor().equals(author)).forEach(System.out::println);
    }

    public double calculateFines(int memberId) {
        return 0.0; // Placeholder
    }
    //DeadCodeEnd
}

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n1. Add Book\n2. Add Member\n3. Borrow Book\n4. Return Book\n5. Display Books\n6. Display Members\n7. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Author: ");
                    String author = scanner.nextLine();
                    System.out.print("ISBN: ");
                    String isbn = scanner.nextLine();
                    library.addBook(new Book(title, author, isbn));
                    break;
                case 2:
                    System.out.print("ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    library.addMember(new Member(id, name));
                    break;
                case 3:
                    System.out.print("ISBN: ");
                    String bIsbn = scanner.nextLine();
                    System.out.print("Member ID: ");
                    int mId1 = scanner.nextInt();
                    library.borrowBook(bIsbn, mId1);
                    break;
                case 4:
                    System.out.print("ISBN: ");
                    String rIsbn = scanner.nextLine();
                    System.out.print("Member ID: ");
                    int mId2 = scanner.nextInt();
                    library.returnBook(rIsbn, mId2);
                    break;
                case 5:
                    library.displayBooks();
                    break;
                case 6:
                    library.displayMembers();
                    break;
                case 7:
                    running = false;
                    break;
            }
        }
        scanner.close();
    }
}
