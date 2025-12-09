package com.example.demo.validator.rules;

public interface ValidationRule<T> {
    void validate(T entity);
}
