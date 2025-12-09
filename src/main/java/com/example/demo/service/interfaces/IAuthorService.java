package com.example.demo.service.interfaces;

import com.example.demo.model.Author;
import java.util.List;

public interface IAuthorService {
    List<Author> getAllAuthors();
    Author getAuthorById(Long id);
    Author createAuthor(Author author);
    Author updateAuthor(Long id, Author author);
    void deleteAuthor(Long id);
}
