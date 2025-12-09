package com.example.demo.integration;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.Category;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for BookController following SOLID principles and clean code practices.
 * Uses TestContainers with real PostgreSQL database.
 * 
 * Single Responsibility Principle: Each test method has one clear purpose
 * Open/Closed Principle: Tests are open for extension via helper methods
 * Liskov Substitution Principle: Uses interfaces and abstractions
 * Interface Segregation Principle: Depends only on needed components
 * Dependency Inversion Principle: Depends on Spring abstractions (RestTemplate, Repositories)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BookControllerIntegrationTest {

    private static final String API_BOOKS_PATH = "/api/books";
    private static final String BOOK_TITLE = "Clean Code";
    private static final Integer BOOK_YEAR = 2008;
    private static final String AUTHOR_NAME = "Robert C. Martin";
    private static final String CATEGORY_NAME = "Programming";

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Author testAuthor;
    private Category testCategory;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
        prepareTestData();
    }

    @AfterEach
    void cleanUp() {
        cleanDatabase();
    }

    @Test
    @DisplayName("POST /api/books - Should create book with valid data and return 201 CREATED")
    void shouldCreateBookWhenValidDataProvided() throws Exception {
        // Given
        Book newBook = createBookWithoutId(BOOK_TITLE, BOOK_YEAR, testAuthor.getId(), testCategory.getId());
        String bookJson = objectMapper.writeValueAsString(newBook);
        HttpHeaders headers = createJsonHeaders();

        // When
        ResponseEntity<Book> createdBookResponse = restTemplate.exchange(
                buildApiUrl(API_BOOKS_PATH),
                HttpMethod.POST,
                new HttpEntity<>(bookJson, headers),
                Book.class
        );

        // Then
        assertThat(createdBookResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createdBookResponse.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        
        Book createdBook = createdBookResponse.getBody();
        assertThat(createdBook).isNotNull();
        assertThat(createdBook.getId()).isNotNull();
        assertThat(createdBook.getTitle()).isEqualTo(BOOK_TITLE);
        assertThat(createdBook.getYear()).isEqualTo(BOOK_YEAR);
        assertThat(createdBook.getAuthorId()).isEqualTo(testAuthor.getId());
        assertThat(createdBook.getCategoryId()).isEqualTo(testCategory.getId());
        
        // Verify persistence
        assertThat(bookRepository.findById(createdBook.getId())).isPresent();
    }

    @Test
    @DisplayName("GET /api/books/{id} - Should retrieve existing book and return 200 OK")
    void shouldRetrieveBookWhenBookExists() {
        // Given
        Book savedBook = saveBookToDatabase(BOOK_TITLE, BOOK_YEAR, testAuthor.getId(), testCategory.getId());
        HttpHeaders headers = createJsonHeaders();

        // When
        ResponseEntity<Book> retrievedBookResponse = restTemplate.exchange(
                buildApiUrl(API_BOOKS_PATH + "/" + savedBook.getId()),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Book.class
        );

        // Then
        assertThat(retrievedBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(retrievedBookResponse.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        
        Book retrievedBook = retrievedBookResponse.getBody();
        assertThat(retrievedBook).isNotNull();
        assertThat(retrievedBook.getId()).isEqualTo(savedBook.getId());
        assertThat(retrievedBook.getTitle()).isEqualTo(BOOK_TITLE);
        assertThat(retrievedBook.getYear()).isEqualTo(BOOK_YEAR);
        assertThat(retrievedBook.getAuthorId()).isEqualTo(testAuthor.getId());
        assertThat(retrievedBook.getCategoryId()).isEqualTo(testCategory.getId());
    }

    // Helper methods following DRY and clean code principles

    /**
     * Builds full API URL with given path.
     * Centralizes URL construction logic.
     */
    private String buildApiUrl(String path) {
        return "http://localhost:" + port + path;
    }

    /**
     * Creates HTTP headers with JSON content type.
     * Encapsulates headers creation logic.
     */
    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    /**
     * Creates a Book object without ID for POST requests.
     * Follows Single Responsibility Principle - only creates Book objects.
     */
    private Book createBookWithoutId(String title, Integer year, Long authorId, Long categoryId) {
        return new Book(title, year, authorId, categoryId);
    }

    /**
     * Saves a book to the database and returns the persisted entity.
     * Encapsulates database interaction logic.
     */
    private Book saveBookToDatabase(String title, Integer year, Long authorId, Long categoryId) {
        Book book = new Book(title, year, authorId, categoryId);
        return bookRepository.save(book);
    }

    /**
     * Prepares test data for each test.
     * Follows Setup pattern for consistent test state.
     */
    private void prepareTestData() {
        testAuthor = authorRepository.save(new Author(AUTHOR_NAME));
        testCategory = categoryRepository.save(new Category(CATEGORY_NAME));
    }

    /**
     * Cleans the database after each test.
     * Ensures test isolation and independence.
     */
    private void cleanDatabase() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();
    }
}
