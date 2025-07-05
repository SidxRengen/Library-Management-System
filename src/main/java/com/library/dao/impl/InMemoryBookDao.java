package main.java.com.library.dao.impl;

import main.java.com.library.dao.BookDao;
import main.java.com.library.models.Book;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryBookDao implements BookDao {
    private final Map<Long, Book> books = new HashMap<>();
    private final Map<String, Book> booksByIsbn = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Book addBook(Book book) {
        long id = idGenerator.getAndIncrement();
        book.setId(id);
        books.put(id, book);
        booksByIsbn.put(book.getIsbn(), book);
        return book;
    }

    @Override
    public Book updateBook(Book book) {
        if (books.containsKey(book.getId())) {
            books.put(book.getId(), book);
            booksByIsbn.put(book.getIsbn(), book);
            return book;
        }
        return null;
    }

    @Override
    public void deleteBook(Long bookId) {
        Book book = books.remove(bookId);
        if (book != null) {
            booksByIsbn.remove(book.getIsbn());
        }
    }

    @Override
    public Book findBookById(Long bookId) {
        return books.get(bookId);
    }

    @Override
    public Book findBookByIsbn(String isbn) {
        return booksByIsbn.get(isbn);
    }

    @Override
    public List<Book> findAllBooks() {
        return new ArrayList<>(books.values());
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public List<Book> findBooksByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public List<Book> findBooksByCategory(Book.BookCategory category) {
        List<Book> result = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getCategory() == category) {
                result.add(book);
            }
        }
        return result;
    }
}