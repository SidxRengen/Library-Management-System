package main.java.com.library;

import main.java.com.library.controllers.LibraryController;
import main.java.com.library.dao.BookDao;
import main.java.com.library.dao.LoanDao;
import main.java.com.library.dao.UserDao;
import main.java.com.library.dao.impl.InMemoryBookDao;
import main.java.com.library.dao.impl.InMemoryLoanDao;
import main.java.com.library.dao.impl.InMemoryUserDao;
import main.java.com.library.services.LibraryService;
import main.java.com.library.services.LibraryServiceImpl;

public class MainApp {
    public static void main(String[] args) {
        // Initialize DAOs
        BookDao bookDao = new InMemoryBookDao();
        UserDao userDao = new InMemoryUserDao();
        LoanDao loanDao = new InMemoryLoanDao();

        // Initialize service
        LibraryService libraryService = new LibraryServiceImpl(bookDao, userDao, loanDao);

        // Initialize controller
        LibraryController controller = new LibraryController(libraryService);

        // Start application
        controller.start();
    }
}