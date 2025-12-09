package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.interfaces.ICategoryService;
import com.example.demo.validator.NameValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final NameValidator nameValidator;

    public CategoryService(CategoryRepository categoryRepository, NameValidator nameValidator) {
        this.categoryRepository = categoryRepository;
        this.nameValidator = nameValidator;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
    }

    public Category createCategory(Category category) {
        nameValidator.validateName(category.getName(), "Category");
        category.setId(null);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category category) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        nameValidator.validateName(category.getName(), "Category");
        category.setId(id);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        categoryRepository.deleteById(id);
    }
}
