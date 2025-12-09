package com.example.demo.validator.rules;

import com.example.demo.exception.InvalidDataException;
import com.example.demo.model.Book;
import com.example.demo.repository.CategoryRepository;

public class CategoryExistsRule implements ValidationRule<Book> {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryExistsRule(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public void validate(Book book) {
        Long categoryId = book.getCategoryId();
        
        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            throw new InvalidDataException("Category with id " + categoryId + " does not exist");
        }
    }
}
