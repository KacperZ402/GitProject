# Instrukcja uruchomienia projektu Biblioteka

## Wymagania

- Java 21
- Docker i Docker Compose
- Maven (lub użyj `mvnw`)

## Uruchomienie PostgreSQL w Dockerze

### 1. Uruchom bazę danych

```powershell
docker-compose up -d
```

Baza danych PostgreSQL zostanie uruchomiona na porcie `5432` z konfiguracją:

- Baza: `library_db`
- Użytkownik: `postgres`
- Hasło: `postgres`

### 2. Sprawdź status kontenera

```powershell
docker ps
```

### 3. Zatrzymanie bazy danych (opcjonalnie)

```powershell
docker-compose down
```

## Uruchomienie aplikacji Spring Boot

```powershell
./mvnw spring-boot:run
```

API będzie dostępne na: `http://localhost:8080`

## Tabele w bazie danych

Hibernate automatycznie utworzy tabele:

- `authors` (id, name)
- `categories` (id, name)
- `books` (id, title, year, author_id, category_id)

## Testowanie API

Zaimportuj plik `Library_API_Postman_Collection.json` do Postmana.

### Przykładowe requesty:

**Utworzenie autora:**

```
POST http://localhost:8080/api/authors
Content-Type: application/json

{
  "name": "Adam Mickiewicz"
}
```

**Utworzenie kategorii:**

```
POST http://localhost:8080/api/categories
Content-Type: application/json

{
  "name": "Poezja"
}
```

**Utworzenie książki:**

```
POST http://localhost:8080/api/books
Content-Type: application/json

{
  "title": "Pan Tadeusz",
  "year": 1834,
  "authorId": 1,
  "categoryId": 1
}
```

## Sprawdzanie danych w bazie

Połącz się z PostgreSQL:

```powershell
psql -U postgres -d library_db
```

Zapytania SQL:

```sql
SELECT * FROM authors;
SELECT * FROM categories;
SELECT * FROM books;
```
