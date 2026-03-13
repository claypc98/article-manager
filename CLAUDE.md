# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the application
./mvnw spring-boot:run

# Build
./mvnw clean install

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ArticleServiceTest

# Run a single test method
./mvnw test -Dtest=ArticleServiceTest#saveArticle_ShouldReturnArticleDTO
```

## Architecture

Standard Spring Boot layered architecture:

**Controller → Service → Repository**

- `ArticleController` — REST controller at `/articles`. Converts `ObjectId` from path variable strings and delegates to `ArticleService`. Catches `ArticleNotFoundException` (→ 404) and `InvalidArticleException` (→ 400).
- `ArticleService` — Business logic. Validates incoming `ArticleDTO`s, uses `ArticleMapper` to convert between `Article` and `ArticleDTO`, and calls `ArticleRepository`.
- `ArticleRepository` — `MongoRepository<Article, ObjectId>` with custom `@Query` methods for tag filtering, title search, and date-based filtering.
- `ArticleMapper` — MapStruct interface (singleton via `ArticleMapper.INSTANCE`) that maps between `Article` (MongoDB document) and `ArticleDTO`.

## Data Model

`Article` is stored in the `articles` MongoDB collection. Fields: `objectId` (MongoDB `_id`), `title`, `content`, `author`, `tags` (List<String>), `createdAt` (LocalDateTime). Bean validation annotations (`@NotBlank`, `@Size`, `@NotNull`) are on the entity but validation is enforced manually in `ArticleService.validateArticle()` (currently only checks title).

`ArticleDTO` mirrors the entity and is what the controller sends/receives as JSON.

## Infrastructure

- **MongoDB**: Requires a running MongoDB instance at `localhost:27017`, database `articledb` (configured in `application.properties`).
- **Java 17**, **Spring Boot 3.3.2**.
- Tests use Mockito to mock `ArticleRepository` — no MongoDB connection required to run tests.
