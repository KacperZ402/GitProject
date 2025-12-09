package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.validator.BookValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Tests")
class BookServiceTest {

    private static final Long FIRST_BOOK_ID = 1L;
    private static final Long SECOND_BOOK_ID = 2L;
    private static final Long CREATED_BOOK_ID = 3L;
    private static final Long NON_EXISTING_BOOK_ID = 999L;
    
    private static final String CLEAN_CODE_TITLE = "Clean Code";
    private static final String TDD_TITLE = "Test Driven Development";
    private static final String REFACTORING_TITLE = "Refactoring";
    
    private static final Integer CLEAN_CODE_YEAR = 2008;
    private static final Integer TDD_YEAR = 2002;
    private static final Integer REFACTORING_YEAR = 1999;
    
    private static final Long FIRST_AUTHOR_ID = 1L;
    private static final Long SECOND_AUTHOR_ID = 2L;
    private static final Long FIRST_CATEGORY_ID = 1L;
    private static final Long SECOND_CATEGORY_ID = 2L;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookValidator bookValidator;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("GET - Should return all books when repository contains books")
    void getAllBooks_WhenBooksExist_ShouldReturnBookList() {
        // Given
        Book firstBook = BookTestBuilder.aBook()
                .withId(FIRST_BOOK_ID)
                .withTitle(CLEAN_CODE_TITLE)
                .withYear(CLEAN_CODE_YEAR)
                .withAuthorId(FIRST_AUTHOR_ID)
                .withCategoryId(FIRST_CATEGORY_ID)
                .build();
        
        Book secondBook = BookTestBuilder.aBook()
                .withId(SECOND_BOOK_ID)
                .withTitle(TDD_TITLE)
                .withYear(TDD_YEAR)
                .withAuthorId(SECOND_AUTHOR_ID)
                .withCategoryId(FIRST_CATEGORY_ID)
                .build();
        
        List<Book> expectedBooks = Arrays.asList(firstBook, secondBook);
        when(bookRepository.findAll()).thenReturn(expectedBooks);

        // When
        List<Book> actualBooks = bookService.getAllBooks();

        // Then
        assertThat(actualBooks)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(firstBook, secondBook);
        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("POST - Should create and return book when valid data is provided")
    void createBook_WithValidData_ShouldReturnCreatedBook() {
        // Given
        Book bookToCreate = BookTestBuilder.aBook()
                .withTitle(REFACTORING_TITLE)
                .withYear(REFACTORING_YEAR)
                .withAuthorId(FIRST_AUTHOR_ID)
                .withCategoryId(SECOND_CATEGORY_ID)
                .build();
        
        Book savedBook = BookTestBuilder.aBook()
                .withId(CREATED_BOOK_ID)
                .withTitle(REFACTORING_TITLE)
                .withYear(REFACTORING_YEAR)
                .withAuthorId(FIRST_AUTHOR_ID)
                .withCategoryId(SECOND_CATEGORY_ID)
                .build();
        
        doNothing().when(bookValidator).validate(bookToCreate);
        when(bookRepository.save(bookToCreate)).thenReturn(savedBook);

        // When
        Book createdBook = bookService.createBook(bookToCreate);

        // Then
        assertThat(createdBook)
                .isNotNull()
                .satisfies(book -> {
                    assertThat(book.getId()).isEqualTo(CREATED_BOOK_ID);
                    assertThat(book.getTitle()).isEqualTo(REFACTORING_TITLE);
                    assertThat(book.getYear()).isEqualTo(REFACTORING_YEAR);
                    assertThat(book.getAuthorId()).isEqualTo(FIRST_AUTHOR_ID);
                    assertThat(book.getCategoryId()).isEqualTo(SECOND_CATEGORY_ID);
                });
        assertThat(bookToCreate.getId()).isNull();
        verify(bookValidator).validate(bookToCreate);
        verify(bookRepository).save(bookToCreate);
    }

    @Test
    @DisplayName("DELETE - Should throw ResourceNotFoundException when deleting non-existing book")
    void deleteBook_WhenBookDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        when(bookRepository.existsById(NON_EXISTING_BOOK_ID)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> bookService.deleteBook(NON_EXISTING_BOOK_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book with id " + NON_EXISTING_BOOK_ID + " not found");
        
        verify(bookRepository).existsById(NON_EXISTING_BOOK_ID);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    private static class BookTestBuilder {
        private Long id;
        private String title;
        private Integer year;
        private Long authorId;
        private Long categoryId;

        private BookTestBuilder() {
        }

        static BookTestBuilder aBook() {
            return new BookTestBuilder();
        }

        BookTestBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        BookTestBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        BookTestBuilder withYear(Integer year) {
            this.year = year;
            return this;
        }

        BookTestBuilder withAuthorId(Long authorId) {
            this.authorId = authorId;
            return this;
        }

        BookTestBuilder withCategoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        Book build() {
            Book book = new Book(title, year, authorId, categoryId);
            book.setId(id);
            return book;
        }
    }
}
