import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Library library = new Library("Central Library");

        // Create books
        Book book1 = new Book("1984", "George Orwell", "978-0451524935", 1949);
        Book book2 = new Book("To Kill a Mockingbird", "Harper Lee", "978-0061120084", 1960);
        Book book3 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", 1925);

        // Create members
        Member member1 = new Member("John Doe", "M001", "john@email.com");
        Member member2 = new Member("Jane Smith", "M002", "jane@email.com");

        // Add to library
        library.addBook(book1);
        library.addBook(book2);
        library.addBook(book3);
        library.registerMember(member1);
        library.registerMember(member2);

        // Perform operations
        System.out.println("=== Library System ===");
        library.displayAvailableBooks();

        System.out.println("\n--- Borrowing books ---");
        library.borrowBook(member1, book1);
        library.borrowBook(member2, book2);

        System.out.println("\n--- Available books after borrowing ---");
        library.displayAvailableBooks();

        System.out.println("\n--- Member borrowing history ---");
        member1.displayBorrowingHistory();

        //DeadCodeStart
        // Initialize analytics but never use them
        LibraryAnalytics analytics = new LibraryAnalytics(library);
        analytics.calculatePopularityScore(book1);
        //DeadCodeEnd
    }
}

// Book.java
class Book {
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;
    private boolean isAvailable;
    private int borrowCount;

    public Book(String title, String author, String isbn, int publicationYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.isAvailable = true;
        this.borrowCount = 0;
    }

    public String getTitle() {
        return title;
    }

    //DeadCodeStart
    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }
    //DeadCodeEnd

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void incrementBorrowCount() {
        borrowCount++;
    }

    //DeadCodeStart
    public int getBorrowCount() {
        return borrowCount;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void updateTitle(String newTitle) {
        // Never used - dead code
        this.title = newTitle;
        System.out.println("Title updated to: " + newTitle);
    }

    private boolean isClassic() {
        // Unused helper method
        return publicationYear < 1950;
    }

    public String getGenre() {
        // Returns hardcoded value, never called
        return "Fiction";
    }
    //DeadCodeEnd

    @Override
    public String toString() {
        return String.format("'%s' by %s [%s] - %s",
                title, author, isbn, "Available");
    }
}

// Member.java
class Member {
    private String name;
    private String memberId;
    private String email;
    private List<Book> borrowedBooks;
    private List<String> borrowingHistory;
    private int totalBorrows;

    public Member(String name, String memberId, String email) {
        this.name = name;
        this.memberId = memberId;
        this.email = email;
        this.borrowedBooks = new ArrayList<>();
        this.borrowingHistory = new ArrayList<>();
        this.totalBorrows = 0;
    }

    public String getName() {
        return name;
    }

    //DeadCodeStart
    public String getMemberId() {
        return memberId;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }
    //DeadCodeEnd

    public void borrowBook(Book book) {
        borrowedBooks.add(book);
        borrowingHistory.add(book.getTitle());
        totalBorrows++;
    }

    //DeadCodeStart
    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }
    //DeadCodeEnd

    public void displayBorrowingHistory() {
        System.out.println("Borrowing history for " + name + ":");
        for (String bookTitle : borrowingHistory) {
            System.out.println("  - " + bookTitle);
        }
    }

    //DeadCodeStart
    public String getEmail() {
        return email;
    }

    public void updateEmail(String newEmail) {
        // Never called
        this.email = newEmail;
        System.out.println("Email updated for " + name);
    }

    public int calculateMembershipLevel() {
        // Complex dead code that's never used
        if (totalBorrows > 50) return 3;
        else if (totalBorrows > 20) return 2;
        else return 1;
    }

    private boolean canBorrowMore() {
        // Unused validation method
        return borrowedBooks.size() < 5;
    }

    public List<String> getRecommendations() {
        // Dead code that returns dummy data
        List<String> recommendations = new ArrayList<>();
        recommendations.add("Recommended Book 1");
        recommendations.add("Recommended Book 2");
        return recommendations;
    }
    //DeadCodeEnd
}

// Library.java
class Library {
    private String name;
    private List<Book> books;
    private List<Member> members;

    public Library(String name) {
        this.name = name;
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
        System.out.println("Added book: " + book.getTitle());
    }

    public void registerMember(Member member) {
        members.add(member);
        System.out.println("Registered member: " + member.getName());
    }

    public void borrowBook(Member member, Book book) {
        if (book.isAvailable()) {
            book.setAvailable(false);
            book.incrementBorrowCount();
            member.borrowBook(book);
            System.out.println(member.getName() + " borrowed " + book.getTitle());
        } else {
            System.out.println("Book is not available: " + book.getTitle());
        }
    }

    //DeadCodeStart
    public void returnBook(Member member, Book book) {
        book.setAvailable(true);
        member.returnBook(book);
        System.out.println(member.getName() + " returned " + book.getTitle());
    }
    //DeadCodeEnd

    public void displayAvailableBooks() {
        System.out.println("Available books in " + name + ":");
        for (Book book : books) {
            if (book.isAvailable()) {
                System.out.println("  " + book);
            }
        }
    }

    //DeadCodeStart
    public List<Book> getBooks() {
        return books;
    }

    public List<Member> getMembers() {
        return members;
    }

    public Book findBookByIsbn(String isbn) {
        // Never used search functionality
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        return null;
    }

    public Member findMemberById(String memberId) {
        // Dead code for finding members
        for (Member member : members) {
            if (member.getMemberId().equals(memberId)) {
                return member;
            }
        }
        return null;
    }

    private void generateMonthlyReport() {
        // Incomplete dead code
        System.out.println("Generating monthly report for " + name);
        int totalBooks = books.size();
        int availableBooks = 0;
        for (Book book : books) {
            if (book.isAvailable()) availableBooks++;
        }
    }

    public void removeBook(String isbn) {
        // Never called removal method
        Book toRemove = findBookByIsbn(isbn);
        if (toRemove != null) {
            books.remove(toRemove);
            System.out.println("Removed book: " + toRemove.getTitle());
        }
    }
    //DeadCodeEnd
}

// LibraryAnalytics.java
//DeadCodeStart
class LibraryAnalytics {
    private Library library;

    public LibraryAnalytics(Library library) {
        this.library = library;
    }

    public double calculatePopularityScore(Book book) {
        // Entire class is dead code - never actually used
        return book.getBorrowCount() * 1.5;
    }

    public List<Book> getMostPopularBooks(int count) {
        List<Book> allBooks = library.getBooks();
        // Incomplete sorting logic
        return allBooks.subList(0, Math.min(count, allBooks.size()));
    }

    public void printStatistics() {
        System.out.println("Library Statistics:");
        System.out.println("Total books: " + library.getBooks().size());
    }
}
//DeadCodeEnd
