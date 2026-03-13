package com.blogger.articleManager;

import com.blogger.articleManager.models.Article;
import com.blogger.articleManager.models.dtos.ArticleSearchCriteria;
import com.blogger.articleManager.repositories.ArticleRepository;
import com.blogger.articleManager.repositories.ArticleRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ArticleRepositoryImplTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private ArticleRepositoryImpl repositoryImpl;

    private static final LocalDateTime JAN = LocalDateTime.of(2024, 1, 15, 0, 0);
    private static final LocalDateTime FEB = LocalDateTime.of(2024, 2, 20, 0, 0);
    private static final LocalDateTime MAR = LocalDateTime.of(2024, 3, 10, 0, 0);
    private static final LocalDateTime APR = LocalDateTime.of(2024, 4, 5, 0, 0);

    @BeforeEach
    void setUp() {
        repositoryImpl = new ArticleRepositoryImpl(mongoTemplate);
        articleRepository.deleteAll();
        articleRepository.saveAll(List.of(
                new Article(null, "Spring Boot Guide",          "Learn Spring Boot basics",             "Alice",   List.of("java", "spring"),             JAN),
                new Article(null, "MongoDB Tutorial",           "NoSQL database introduction",           "Bob",     List.of("mongodb", "nosql"),            FEB),
                new Article(null, "REST API Design",            "Design patterns for REST APIs",         "Alice",   List.of("api", "rest", "java"),         MAR),
                new Article(null, "Microservices Architecture", "Building microservices with Spring",    "Charlie", List.of("spring", "microservices"),     APR)
        ));
    }

    // --- No filters ---

    @Test
    void search_withNoFilters_returnsAllArticles() {
        var results = repositoryImpl.search(new ArticleSearchCriteria());
        assertEquals(4, results.size());
    }

    // --- query (full-text across title / content / author) ---

    @Test
    void search_byQueryMatchingTitle_returnsMatchingArticle() {
        var criteria = new ArticleSearchCriteria();
        criteria.setQuery("MongoDB");

        var results = repositoryImpl.search(criteria);

        assertEquals(1, results.size());
        assertEquals("MongoDB Tutorial", results.get(0).getTitle());
    }

    @Test
    void search_byQueryMatchingContent_returnsMatchingArticle() {
        var criteria = new ArticleSearchCriteria();
        criteria.setQuery("NoSQL");

        var results = repositoryImpl.search(criteria);

        assertEquals(1, results.size());
        assertEquals("MongoDB Tutorial", results.get(0).getTitle());
    }

    @Test
    void search_byQueryMatchingAuthor_returnsAllArticlesByThatAuthor() {
        var criteria = new ArticleSearchCriteria();
        criteria.setQuery("Alice");

        var results = repositoryImpl.search(criteria);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(a -> a.getAuthor().equals("Alice")));
    }

    @Test
    void search_byQuery_isCaseInsensitive() {
        var criteria = new ArticleSearchCriteria();
        criteria.setQuery("spring boot");

        var results = repositoryImpl.search(criteria);

        assertEquals(1, results.size());
        assertEquals("Spring Boot Guide", results.get(0).getTitle());
    }

    @Test
    void search_byQueryWithNoMatch_returnsEmpty() {
        var criteria = new ArticleSearchCriteria();
        criteria.setQuery("nonexistent_xyz_123");

        var results = repositoryImpl.search(criteria);

        assertTrue(results.isEmpty());
    }

    @Test
    void search_withBlankQuery_treatedAsNoFilter() {
        var criteria = new ArticleSearchCriteria();
        criteria.setQuery("   ");

        var results = repositoryImpl.search(criteria);

        assertEquals(4, results.size());
    }

    // --- author (exact match filter) ---

    @Test
    void search_byExactAuthor_returnsOnlyThatAuthor() {
        var criteria = new ArticleSearchCriteria();
        criteria.setAuthor("Bob");

        var results = repositoryImpl.search(criteria);

        assertEquals(1, results.size());
        assertEquals("Bob", results.get(0).getAuthor());
    }

    @Test
    void search_byAuthorWithMultipleArticles_returnsAll() {
        var criteria = new ArticleSearchCriteria();
        criteria.setAuthor("Alice");

        var results = repositoryImpl.search(criteria);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(a -> "Alice".equals(a.getAuthor())));
    }

    @Test
    void search_byAuthorNoMatch_returnsEmpty() {
        var criteria = new ArticleSearchCriteria();
        criteria.setAuthor("Nobody");

        assertTrue(repositoryImpl.search(criteria).isEmpty());
    }

    // --- tags (any-match) ---

    @Test
    void search_bySingleTag_returnsArticlesContainingTag() {
        var criteria = new ArticleSearchCriteria();
        criteria.setTags(List.of("java"));

        var results = repositoryImpl.search(criteria);

        assertEquals(2, results.size()); // Spring Boot Guide + REST API Design
    }

    @Test
    void search_byMultipleTags_returnsArticlesWithAnyOfTheTags() {
        var criteria = new ArticleSearchCriteria();
        criteria.setTags(List.of("mongodb", "rest"));

        var results = repositoryImpl.search(criteria);

        assertEquals(2, results.size()); // MongoDB Tutorial + REST API Design
    }

    @Test
    void search_byTagNoMatch_returnsEmpty() {
        var criteria = new ArticleSearchCriteria();
        criteria.setTags(List.of("python"));

        assertTrue(repositoryImpl.search(criteria).isEmpty());
    }

    // --- date range ---

    @Test
    void search_byCreatedAfter_returnsNewerArticles() {
        var criteria = new ArticleSearchCriteria();
        criteria.setCreatedAfter(LocalDateTime.of(2024, 3, 1, 0, 0));

        var results = repositoryImpl.search(criteria);

        assertEquals(2, results.size()); // MAR + APR
    }

    @Test
    void search_byCreatedBefore_returnsOlderArticles() {
        var criteria = new ArticleSearchCriteria();
        criteria.setCreatedBefore(LocalDateTime.of(2024, 2, 28, 0, 0));

        var results = repositoryImpl.search(criteria);

        assertEquals(2, results.size()); // JAN + FEB
    }

    @Test
    void search_byDateRange_returnsBoundedArticles() {
        var criteria = new ArticleSearchCriteria();
        criteria.setCreatedAfter(LocalDateTime.of(2024, 2, 1, 0, 0));
        criteria.setCreatedBefore(LocalDateTime.of(2024, 3, 31, 0, 0));

        var results = repositoryImpl.search(criteria);

        assertEquals(2, results.size()); // FEB + MAR
    }

    // --- combined filters ---

    @Test
    void search_queryAndAuthor_bothFiltersApplied() {
        var criteria = new ArticleSearchCriteria();
        criteria.setQuery("Spring"); // matches "Spring Boot Guide" (Alice) and "Microservices Architecture" (Charlie, content has "Spring")
        criteria.setAuthor("Alice");

        var results = repositoryImpl.search(criteria);

        assertEquals(1, results.size());
        assertEquals("Spring Boot Guide", results.get(0).getTitle());
    }

    @Test
    void search_authorAndTag_bothFiltersApplied() {
        var criteria = new ArticleSearchCriteria();
        criteria.setAuthor("Alice");
        criteria.setTags(List.of("api"));

        var results = repositoryImpl.search(criteria);

        assertEquals(1, results.size());
        assertEquals("REST API Design", results.get(0).getTitle());
    }

    @Test
    void search_queryAndDateRange_bothFiltersApplied() {
        var criteria = new ArticleSearchCriteria();
        criteria.setQuery("Spring");
        criteria.setCreatedAfter(LocalDateTime.of(2024, 3, 1, 0, 0));

        var results = repositoryImpl.search(criteria);

        // "Microservices Architecture" content has "Spring" and was created in APR
        assertEquals(1, results.size());
        assertEquals("Microservices Architecture", results.get(0).getTitle());
    }

    // --- sorting ---

    @Test
    void search_sortByCreatedAtDesc_isDefault() {
        var results = repositoryImpl.search(new ArticleSearchCriteria());

        assertEquals("Microservices Architecture", results.get(0).getTitle()); // APR newest
        assertEquals("Spring Boot Guide", results.get(3).getTitle());          // JAN oldest
    }

    @Test
    void search_sortByTitleAsc_returnsAlphabeticOrder() {
        var criteria = new ArticleSearchCriteria();
        criteria.setSortBy("title");
        criteria.setSortDir("asc");

        var results = repositoryImpl.search(criteria);

        assertEquals("Microservices Architecture", results.get(0).getTitle());
        assertEquals("Spring Boot Guide", results.get(3).getTitle());
    }

    @Test
    void search_sortByAuthorAsc_groupsByAuthor() {
        var criteria = new ArticleSearchCriteria();
        criteria.setSortBy("author");
        criteria.setSortDir("asc");

        var results = repositoryImpl.search(criteria);

        // Alice < Bob < Charlie
        assertEquals("Alice", results.get(0).getAuthor());
        assertEquals("Charlie", results.get(3).getAuthor());
    }

    @Test
    void search_invalidSortByField_defaultsToCreatedAt() {
        var criteria = new ArticleSearchCriteria();
        criteria.setSortBy("injectedField; drop collection");
        criteria.setSortDir("desc");

        var results = repositoryImpl.search(criteria);

        // Should still return all articles sorted by createdAt desc
        assertEquals(4, results.size());
        assertEquals("Microservices Architecture", results.get(0).getTitle());
    }
}
