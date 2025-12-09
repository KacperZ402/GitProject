package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.interfaces.IBookService;
import com.example.demo.validator.BookValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService implements IBookService {
    private final BookRepository bookRepository;
    private final BookValidator bookValidator;

    public BookService(BookRepository bookRepository, BookValidator bookValidator) {
        this.bookRepository = bookRepository;
        this.bookValidator = bookValidator;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found"));
    }

    public Book createBook(Book book) {
        bookValidator.validate(book);
        book.setId(null);
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book book) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book with id " + id + " not found");
        }
        bookValidator.validate(book);
        book.setId(id);
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book with id " + id + " not found");
        }
        bookRepository.deleteById(id);
    }
}
