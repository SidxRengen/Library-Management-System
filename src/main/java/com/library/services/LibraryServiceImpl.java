package main.java.com.library.services;
import main.java.com.library.dao.BookDao;
import main.java.com.library.dao.LoanDao;
import main.java.com.library.dao.UserDao;
import main.java.com.library.exceptions.BookNotFoundException;
import main.java.com.library.exceptions.LoanLimitExceededException;
import main.java.com.library.exceptions.UserNotFoundException;
import main.java.com.library.models.Book;
import main.java.com.library.models.Loan;
import main.java.com.library.models.User;
import main.java.com.library.utils.DateUtils;
import java.time.LocalDate;
import java.util.List;

public class LibraryServiceImpl implements LibraryService {
    private final BookDao bookDao;
    private final UserDao userDao;
    private final LoanDao loanDao;

    private static final int STUDENT_LOAN_LIMIT = 5;
    private static final int FACULTY_LOAN_LIMIT = 10;
    private static final int STAFF_LOAN_LIMIT = 7;
    private static final int GUEST_LOAN_LIMIT = 2;
    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14;
    private static final int FACULTY_LOAN_PERIOD_DAYS = 30;

    public LibraryServiceImpl(BookDao bookDao, UserDao userDao, LoanDao loanDao) {
        this.bookDao = bookDao;
        this.userDao = userDao;
        this.loanDao = loanDao;
    }

    @Override
    public List<Book> searchBooksByCategory(Book.BookCategory category) {
        return bookDao.findBooksByCategory(category);
    }

    @Override
    public Book addBook(Book book) {
        return bookDao.addBook(book);
    }

    @Override
    public Book updateBook(Book book) throws BookNotFoundException {
        Book existingBook = bookDao.findBookById(book.getId());
        if (existingBook == null) {
            throw new BookNotFoundException("Book with ID " + book.getId() + " not found");
        }
        return bookDao.updateBook(book);
    }

    @Override
    public void removeBook(Long bookId) throws BookNotFoundException {
        Book book = bookDao.findBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found");
        }
        bookDao.deleteBook(bookId);
    }

    @Override
    public Book getBookById(Long bookId) throws BookNotFoundException {
        Book book = bookDao.findBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found");
        }
        return book;
    }

    @Override
    public Book getBookByIsbn(String isbn) throws BookNotFoundException {
        Book book = bookDao.findBookByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException("Book with ISBN " + isbn + " not found");
        }
        return book;
    }

    @Override
    public List<Book> searchBooksByTitle(String title) {
        return bookDao.findBooksByTitle(title);
    }

    @Override
    public List<Book> searchBooksByAuthor(String author) {
        return bookDao.findBooksByAuthor(author);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookDao.findAllBooks();
    }

    @Override
    public User registerUser(User user) {
        return userDao.addUser(user);
    }

    @Override
    public User updateUser(User user) throws UserNotFoundException {
        User existingUser = userDao.findUserById(user.getUserId());
        if (existingUser == null) {
            throw new UserNotFoundException("User with ID " + user.getUserId() + " not found");
        }
        return userDao.updateUser(user);
    }

    @Override
    public void unregisterUser(String userId) throws UserNotFoundException {
        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        userDao.deleteUser(userId);
    }

    @Override
    public User getUserById(String userId) throws UserNotFoundException {
        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAllUsers();
    }

    @Override
    public Loan borrowBook(String userId, Long bookId)
            throws UserNotFoundException, BookNotFoundException, LoanLimitExceededException {

        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }

        Book book = bookDao.findBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found");
        }

        if (book.getQuantity() <= 0) {
            throw new BookNotFoundException("Book with ID " + bookId + " is not available");
        }

        int currentLoans = getUserLoanCount(userId);
        int maxLoans = getLoanLimitForUserType(user.getUserType());

        if (currentLoans >= maxLoans) {
            throw new LoanLimitExceededException("User has reached the maximum loan limit of " + maxLoans);
        }

        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = calculateDueDate(loanDate, user.getUserType());

        Loan loan = new Loan(book, user, loanDate, dueDate);
        loan = loanDao.addLoan(loan);

        // Update book quantity
        book.setQuantity(book.getQuantity() - 1);
        bookDao.updateBook(book);

        return loan;
    }

    @Override
    public void returnBook(Long loanId) throws BookNotFoundException {
        Loan loan = loanDao.findAllLoans().stream()
                .filter(l -> l.getId().equals(loanId))
                .findFirst()
                .orElse(null);

        if (loan == null) {
            throw new BookNotFoundException("Loan with ID " + loanId + " not found");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(Loan.LoanStatus.RETURNED);
        loanDao.updateLoan(loan);

        // Update book quantity
        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookDao.updateBook(book);
    }

    @Override
    public List<Loan> getUserLoans(String userId) throws UserNotFoundException {
        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        return loanDao.findLoansByUser(userId);
    }

    @Override
    public List<Loan> getOverdueLoans() {
        return loanDao.findOverdueLoans();
    }

    @Override
    public List<Loan> getAllActiveLoans() {
        return loanDao.findAllLoans().stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .toList();
    }

    @Override
    public boolean isBookAvailable(Long bookId) {
        try {
            Book book = getBookById(bookId);
            return book.getQuantity() > 0;
        } catch (BookNotFoundException e) {
            return false;
        }
    }

    @Override
    public int getUserLoanCount(String userId) {
        return loanDao.findActiveLoansByUser(userId).size();
    }

    @Override
    public int getAvailableBookCount(Long bookId) {
        try {
            Book book = getBookById(bookId);
            return book.getQuantity();
        } catch (BookNotFoundException e) {
            return 0;
        }
    }

    private int getLoanLimitForUserType(User.UserType userType) {
        return switch (userType) {
            case STUDENT -> STUDENT_LOAN_LIMIT;
            case FACULTY -> FACULTY_LOAN_LIMIT;
            case STAFF -> STAFF_LOAN_LIMIT;
            case GUEST -> GUEST_LOAN_LIMIT;
        };
    }

    private LocalDate calculateDueDate(LocalDate loanDate, User.UserType userType) {
        int loanPeriod = userType == User.UserType.FACULTY ?
                FACULTY_LOAN_PERIOD_DAYS : DEFAULT_LOAN_PERIOD_DAYS;
        return loanDate.plusDays(loanPeriod);
    }
}
