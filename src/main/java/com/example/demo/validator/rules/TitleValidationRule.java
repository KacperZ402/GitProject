package com.example.demo.validator.rules;

import com.example.demo.exception.InvalidDataException;
import com.example.demo.model.Book;

public class TitleValidationRule implements ValidationRule<Book> {
    
    private static final int MIN_TITLE_LENGTH = 2;
    private static final int MAX_TITLE_LENGTH = 100;
    
    @Override
    public void validate(Book book) {
        String title = book.getTitle();
        
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDataException("Book title cannot be empty");
        }
        
        if (title.length() < MIN_TITLE_LENGTH || title.length() > MAX_TITLE_LENGTH) {
            throw new InvalidDataException(
                "Book title must be between " + MIN_TITLE_LENGTH + 
                " and " + MAX_TITLE_LENGTH + " characters"
            );
        }
    }
}
