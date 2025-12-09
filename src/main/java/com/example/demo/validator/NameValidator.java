package com.example.demo.validator;

import com.example.demo.exception.InvalidDataException;
import org.springframework.stereotype.Component;

@Component
public class NameValidator {
    
    public void validateName(String name, String entityType) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException(entityType + " name cannot be empty");
        }
    }
}
