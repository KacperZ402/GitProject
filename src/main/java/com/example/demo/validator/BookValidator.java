package com.example.demo.validator;

import org.springframework.stereotype.Component;

@Component
public class BookValidator {
    private final NameValidator nameValidator;
    private final ReferenceValidator referenceValidator;

    public BookValidator(NameValidator nameValidator, ReferenceValidator referenceValidator) {
        this.nameValidator = nameValidator;
        this.referenceValidator = referenceValidator;
    }

    public void validate(String title, Long authorId, Long categoryId) {
        nameValidator.validateName(title, "Book title");
        referenceValidator.validateAuthorExists(authorId);
        referenceValidator.validateCategoryExists(categoryId);
    }
}
