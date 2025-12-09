package com.example.demo.validator;

import com.example.demo.exception.InvalidDataException;
import org.springframework.stereotype.Component;

@Component
public class NameValidator {
    
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 100;
    
    public void validateName(String name, String entityType) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException(entityType + " name cannot be empty");
        }
        
        if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            throw new InvalidDataException(
                entityType + " name must be between " + MIN_NAME_LENGTH + 
                " and " + MAX_NAME_LENGTH + " characters"
            );
        }
    }
}
