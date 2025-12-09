package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.validator.NameValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final NameValidator nameValidator;

    public AuthorService(AuthorRepository authorRepository, NameValidator nameValidator) {
        this.authorRepository = authorRepository;
        this.nameValidator = nameValidator;
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " not found"));
    }

    public Author createAuthor(Author author) {
        nameValidator.validateName(author.getName(), "Author");
        author.setId(null);
        return authorRepository.save(author);
    }

    public Author updateAuthor(Long id, Author author) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author with id " + id + " not found");
        }
        nameValidator.validateName(author.getName(), "Author");
        author.setId(id);
        return authorRepository.save(author);
    }

    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author with id " + id + " not found");
        }
        authorRepository.deleteById(id);
    }
}
