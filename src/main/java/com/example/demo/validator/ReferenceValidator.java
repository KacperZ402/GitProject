package com.example.demo.validator;

import com.example.demo.exception.InvalidDataException;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class ReferenceValidator {
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public ReferenceValidator(AuthorRepository authorRepository, CategoryRepository categoryRepository) {
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    public void validateAuthorExists(Long authorId) {
        if (authorId != null && !authorRepository.existsById(authorId)) {
            throw new InvalidDataException("Author with id " + authorId + " does not exist");
        }
    }

    public void validateCategoryExists(Long categoryId) {
        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            throw new InvalidDataException("Category with id " + categoryId + " does not exist");
        }
    }
}
