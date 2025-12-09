package com.example.demo.validator.rules;

import com.example.demo.exception.InvalidDataException;
import com.example.demo.model.Book;
import com.example.demo.repository.AuthorRepository;

public class AuthorExistsRule implements ValidationRule<Book> {
    
    private final AuthorRepository authorRepository;
    
    public AuthorExistsRule(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }
    
    @Override
    public void validate(Book book) {
        Long authorId = book.getAuthorId();
        
        if (authorId != null && !authorRepository.existsById(authorId)) {
            throw new InvalidDataException("Author with id " + authorId + " does not exist");
        }
    }
}
