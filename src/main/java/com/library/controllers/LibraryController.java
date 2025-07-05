package main.java.com.library.controllers;

import main.java.com.library.exceptions.BookNotFoundException;
import main.java.com.library.exceptions.LoanLimitExceededException;
import main.java.com.library.exceptions.UserNotFoundException;
import main.java.com.library.models.Book;
import main.java.com.library.models.Loan;
import main.java.com.library.models.User;
import main.java.com.library.services.LibraryService;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class LibraryController {
    private final LibraryService libraryService;
    private final Scanner scanner;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Library Management System ===");
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> manageBooks();
                case 2 -> manageUsers();
                case 3 -> manageLoans();
                case 4 -> generateReports();
                case 0 -> {
                    System.out.println("Exiting system...");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Book Management");
        System.out.println("2. User Management");
        System.out.println("3. Loan Management");
        System.out.println("4. Reports");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private void manageBooks() {
        boolean back = false;
        while (!back) {
            System.out.println("\nBook Management:");
            System.out.println("1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. Remove Book");
            System.out.println("4. Search Books");
            System.out.println("5. List All Books");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addBook();
                case 2 -> updateBook();
                case 3 -> removeBook();
                case 4 -> searchBooks();
                case 5 -> listAllBooks();
                case 0 -> back = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void addBook() {
        System.out.println("\nAdd New Book:");
        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Author: ");
        String author = scanner.nextLine();

        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();

        System.out.print("Publication Year: ");
        int year = scanner.nextInt();

        System.out.print("Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Categories: 1-FICTION, 2-NON_FICTION, 3-SCIENCE, 4-HISTORY, 5-BIOGRAPHY");
        System.out.print("Category: ");
        int categoryChoice = scanner.nextInt();
        scanner.nextLine();

        Book.BookCategory category = switch (categoryChoice) {
            case 1 -> Book.BookCategory.FICTION;
            case 2 -> Book.BookCategory.NON_FICTION;
            case 3 -> Book.BookCategory.SCIENCE;
            case 4 -> Book.BookCategory.HISTORY;
            case 5 -> Book.BookCategory.BIOGRAPHY;
            default -> Book.BookCategory.FICTION;
        };

        Book book = new Book(title, author, isbn, year, quantity, category);
        libraryService.addBook(book);
        System.out.println("Book added successfully!");
    }

    private void updateBook() {
        System.out.print("\nEnter Book ID to update: ");
        Long bookId = scanner.nextLong();
        scanner.nextLine();

        try {
            Book existingBook = libraryService.getBookById(bookId);
            System.out.println("Current Book Details:");
            System.out.println(existingBook);

            System.out.println("\nEnter new details (leave blank to keep current):");

            System.out.print("Title [" + existingBook.getTitle() + "]: ");
            String title = scanner.nextLine();
            if (!title.isEmpty()) existingBook.setTitle(title);

            System.out.print("Author [" + existingBook.getAuthor() + "]: ");
            String author = scanner.nextLine();
            if (!author.isEmpty()) existingBook.setAuthor(author);

            System.out.print("Quantity [" + existingBook.getQuantity() + "]: ");
            String quantityInput = scanner.nextLine();
            if (!quantityInput.isEmpty()) {
                existingBook.setQuantity(Integer.parseInt(quantityInput));
            }

            libraryService.updateBook(existingBook);
            System.out.println("Book updated successfully!");
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeBook() {
        System.out.print("\nEnter Book ID to remove: ");
        Long bookId = scanner.nextLong();
        scanner.nextLine();

        try {
            libraryService.removeBook(bookId);
            System.out.println("Book removed successfully!");
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchBooks() {
        System.out.println("\nSearch Books By:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. Category");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        List<Book> results;
        switch (choice) {
            case 1 -> {
                System.out.print("Enter title: ");
                String title = scanner.nextLine();
                results = libraryService.searchBooksByTitle(title);
            }
            case 2 -> {
                System.out.print("Enter author: ");
                String author = scanner.nextLine();
                results = libraryService.searchBooksByAuthor(author);
            }
            case 3 -> {
                System.out.println("Categories: 1-FICTION, 2-NON_FICTION, 3-SCIENCE, 4-HISTORY, 5-BIOGRAPHY");
                System.out.print("Enter category: ");
                int categoryChoice = scanner.nextInt();
                scanner.nextLine();
                Book.BookCategory category = switch (categoryChoice) {
                    case 1 -> Book.BookCategory.FICTION;
                    case 2 -> Book.BookCategory.NON_FICTION;
                    case 3 -> Book.BookCategory.SCIENCE;
                    case 4 -> Book.BookCategory.HISTORY;
                    case 5 -> Book.BookCategory.BIOGRAPHY;
                    default -> Book.BookCategory.FICTION;
                };
                results = libraryService.searchBooksByCategory(category);
            }
            default -> {
                System.out.println("Invalid option.");
                return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            System.out.println("\nSearch Results:");
            results.forEach(System.out::println);
        }
    }

    private void listAllBooks() {
        List<Book> books = libraryService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books available.");
        } else {
            System.out.println("\nAll Books:");
            books.forEach(System.out::println);
        }
    }

    private void manageUsers() {
        boolean back = false;
        while (!back) {
            System.out.println("\nUser Management:");
            System.out.println("1. Register User");
            System.out.println("2. Update User");
            System.out.println("3. Unregister User");
            System.out.println("4. Find User");
            System.out.println("5. List All Users");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> registerUser();
                case 2 -> updateUser();
                case 3 -> unregisterUser();
                case 4 -> findUser();
                case 5 -> listAllUsers();
                case 0 -> back = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void registerUser() {
        System.out.println("\nRegister New User:");
        System.out.print("User ID: ");
        String userId = scanner.nextLine();

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.println("User Types: 1-STUDENT, 2-FACULTY, 3-STAFF, 4-GUEST");
        System.out.print("User Type: ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();

        User.UserType userType = switch (typeChoice) {
            case 1 -> User.UserType.STUDENT;
            case 2 -> User.UserType.FACULTY;
            case 3 -> User.UserType.STAFF;
            case 4 -> User.UserType.GUEST;
            default -> User.UserType.STUDENT;
        };

        User user = new User(userId, name, email, userType);
        libraryService.registerUser(user);
        System.out.println("User registered successfully!");
    }

    private void updateUser() {
        System.out.print("\nEnter User ID to update: ");
        String userId = scanner.nextLine();

        try {
            User existingUser = libraryService.getUserById(userId);
            System.out.println("Current User Details:");
            System.out.println(existingUser);

            System.out.println("\nEnter new details (leave blank to keep current):");

            System.out.print("Name [" + existingUser.getName() + "]: ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) existingUser.setName(name);

            System.out.print("Email [" + existingUser.getEmail() + "]: ");
            String email = scanner.nextLine();
            if (!email.isEmpty()) existingUser.setEmail(email);

            libraryService.updateUser(existingUser);
            System.out.println("User updated successfully!");
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void unregisterUser() {
        System.out.print("\nEnter User ID to unregister: ");
        String userId = scanner.nextLine();

        try {
            libraryService.unregisterUser(userId);
            System.out.println("User unregistered successfully!");
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void findUser() {
        System.out.print("\nEnter User ID to find: ");
        String userId = scanner.nextLine();

        try {
            User user = libraryService.getUserById(userId);
            System.out.println("\nUser Details:");
            System.out.println(user);
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listAllUsers() {
        List<User> users = libraryService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users registered.");
        } else {
            System.out.println("\nAll Users:");
            users.forEach(System.out::println);
        }
    }

    private void manageLoans() {
        boolean back = false;
        while (!back) {
            System.out.println("\nLoan Management:");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. View User Loans");
            System.out.println("4. View All Active Loans");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> borrowBook();
                case 2 -> returnBook();
                case 3 -> viewUserLoans();
                case 4 -> viewActiveLoans();
                case 0 -> back = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void borrowBook() {
        System.out.println("\nBorrow a Book:");
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();

        System.out.print("Enter Book ID: ");
        Long bookId = scanner.nextLong();
        scanner.nextLine();

        try {
            Loan loan = libraryService.borrowBook(userId, bookId);
            System.out.println("Book borrowed successfully!");
            System.out.println("Due Date: " + loan.getDueDate());
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (LoanLimitExceededException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void returnBook() {
        System.out.println("\nReturn a Book:");
        System.out.print("Enter Loan ID: ");
        Long loanId = scanner.nextLong();
        scanner.nextLine();

        try {
            libraryService.returnBook(loanId);
            System.out.println("Book returned successfully!");
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewUserLoans() {
        System.out.print("\nEnter User ID to view loans: ");
        String userId = scanner.nextLine();

        try {
            List<Loan> loans = libraryService.getUserLoans(userId);
            if (loans.isEmpty()) {
                System.out.println("No loans found for this user.");
            } else {
                System.out.println("\nUser Loans:");
                loans.forEach(loan -> {
                    System.out.println(loan);
                    if (loan.getStatus() == Loan.LoanStatus.ACTIVE && loan.getDueDate().isBefore(LocalDate.now())) {
                        System.out.println("  [OVERDUE]");
                    }
                });
            }
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewActiveLoans() {
        List<Loan> activeLoans = libraryService.getAllActiveLoans();
        if (activeLoans.isEmpty()) {
            System.out.println("No active loans.");
        } else {
            System.out.println("\nAll Active Loans:");
            activeLoans.forEach(loan -> {
                System.out.println(loan);
                if (loan.getDueDate().isBefore(LocalDate.now())) {
                    System.out.println("  [OVERDUE]");
                }
            });
        }
    }

    private void generateReports() {
        boolean back = false;
        while (!back) {
            System.out.println("\nReports:");
            System.out.println("1. Overdue Loans");
            System.out.println("2. Available Books");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> overdueLoansReport();
                case 2 -> availableBooksReport();
                case 0 -> back = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void overdueLoansReport() {
        List<Loan> overdueLoans = libraryService.getOverdueLoans();
        if (overdueLoans.isEmpty()) {
            System.out.println("No overdue loans.");
        } else {
            System.out.println("\nOverdue Loans:");
            overdueLoans.forEach(loan -> {
                System.out.println(loan);
                System.out.println("  Days overdue: " +
                        (LocalDate.now().toEpochDay() - loan.getDueDate().toEpochDay()));
            });
        }
    }

    private void availableBooksReport() {
        List<Book> books = libraryService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books available.");
        } else {
            System.out.println("\nAvailable Books:");
            books.stream()
                    .filter(book -> book.getQuantity() > 0)
                    .forEach(book -> System.out.println(book.getTitle() +
                            " (" + book.getQuantity() + " available)"));
        }
    }
}