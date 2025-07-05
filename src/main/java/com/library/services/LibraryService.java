package main.java.com.library.services;

import main.java.com.library.exceptions.BookNotFoundException;
import main.java.com.library.exceptions.LoanLimitExceededException;
import main.java.com.library.exceptions.UserNotFoundException;
import main.java.com.library.models.Book;
import main.java.com.library.models.Loan;
import main.java.com.library.models.User;

import java.time.LocalDate;
import java.util.List;

public interface LibraryService {
    // Book operations
    Book addBook(Book book);
    Book updateBook(Book book) throws BookNotFoundException;
    void removeBook(Long bookId) throws BookNotFoundException;
    Book getBookById(Long bookId) throws BookNotFoundException;
    Book getBookByIsbn(String isbn) throws BookNotFoundException;
    List<Book> searchBooksByTitle(String title);
    List<Book> searchBooksByAuthor(String author);
    List<Book> getAllBooks();
    List<Book> searchBooksByCategory(Book.BookCategory category);

    // User operations
    User registerUser(User user);
    User updateUser(User user) throws UserNotFoundException;
    void unregisterUser(String userId) throws UserNotFoundException;
    User getUserById(String userId) throws UserNotFoundException;
    List<User> getAllUsers();

    // Loan operations
    Loan borrowBook(String userId, Long bookId)
            throws UserNotFoundException, BookNotFoundException, LoanLimitExceededException;
    void returnBook(Long loanId) throws BookNotFoundException;
    List<Loan> getUserLoans(String userId) throws UserNotFoundException;
    List<Loan> getOverdueLoans();
    List<Loan> getAllActiveLoans();

    // Utility operations
    boolean isBookAvailable(Long bookId);
    int getUserLoanCount(String userId);
    int getAvailableBookCount(Long bookId);
}