package main.java.com.library.dao;

import main.java.com.library.models.Book;
import java.util.List;

public interface BookDao {
    Book addBook(Book book);
    Book updateBook(Book book);
    void deleteBook(Long bookId);
    Book findBookById(Long bookId);
    Book findBookByIsbn(String isbn);
    List<Book> findAllBooks();
    List<Book> findBooksByTitle(String title);
    List<Book> findBooksByAuthor(String author);
    List<Book> findBooksByCategory(Book.BookCategory category);
}