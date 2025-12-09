package com.example.demo.validator;

import com.example.demo.model.Book;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.validator.rules.AuthorExistsRule;
import com.example.demo.validator.rules.CategoryExistsRule;
import com.example.demo.validator.rules.TitleValidationRule;
import com.example.demo.validator.rules.ValidationRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookValidator {
    private final List<ValidationRule<Book>> rules;

    public BookValidator(AuthorRepository authorRepository, CategoryRepository categoryRepository) {
        this.rules = List.of(
            new TitleValidationRule(),
            new AuthorExistsRule(authorRepository),
            new CategoryExistsRule(categoryRepository)
        );
    }

    public void validate(Book book) {
        rules.forEach(rule -> rule.validate(book));
    }
}
