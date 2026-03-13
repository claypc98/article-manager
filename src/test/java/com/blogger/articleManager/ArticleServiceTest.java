package com.blogger.articleManager;

import com.blogger.articleManager.models.Article;
import com.blogger.articleManager.models.dtos.ArticleDTO;
import com.blogger.articleManager.models.dtos.ArticleSearchCriteria;
import com.blogger.articleManager.repositories.ArticleRepository;
import com.blogger.articleManager.service.ArticleService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void saveArticle_ShouldReturnArticleDTO(){
        ArticleDTO articleDTO = new ArticleDTO("1", "Title", "Content", "Author", List.of("Tag"),LocalDateTime.now());
        Article article = new Article(new ObjectId("60c72b2f4f1a2b001f6471d1"), "Title", "Content", "Author", List.of("Tag"), LocalDateTime.now());
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        ArticleDTO savedArticleDTO = articleService.saveArticle(articleDTO);

        assertNotNull(savedArticleDTO);
        assertEquals(articleDTO.getTitle(), savedArticleDTO.getTitle());
    }

    // --- search ---

    @Test
    public void search_delegatesToRepositoryAndMapsResults() {
        ArticleSearchCriteria criteria = new ArticleSearchCriteria();
        criteria.setQuery("spring");

        Article article = new Article(new ObjectId(), "Spring Guide", "Content", "Alice", List.of("java"), LocalDateTime.now());
        when(articleRepository.search(criteria)).thenReturn(List.of(article));

        List<ArticleDTO> results = articleService.search(criteria);

        assertEquals(1, results.size());
        assertEquals("Spring Guide", results.get(0).getTitle());
        assertEquals("Alice", results.get(0).getAuthor());
        verify(articleRepository).search(criteria);
    }

    @Test
    public void search_emptyRepositoryResults_returnsEmptyList() {
        ArticleSearchCriteria criteria = new ArticleSearchCriteria();
        when(articleRepository.search(criteria)).thenReturn(List.of());

        List<ArticleDTO> results = articleService.search(criteria);

        assertTrue(results.isEmpty());
        verify(articleRepository).search(criteria);
    }

    @Test
    public void search_multipleResults_allMappedToDTOs() {
        ArticleSearchCriteria criteria = new ArticleSearchCriteria();
        criteria.setAuthor("Alice");

        List<Article> articles = List.of(
                new Article(new ObjectId(), "Article One", "Content", "Alice", List.of("tag1"), LocalDateTime.now()),
                new Article(new ObjectId(), "Article Two", "Content", "Alice", List.of("tag2"), LocalDateTime.now())
        );
        when(articleRepository.search(criteria)).thenReturn(articles);

        List<ArticleDTO> results = articleService.search(criteria);

        assertEquals(2, results.size());
        assertEquals("Article One", results.get(0).getTitle());
        assertEquals("Article Two", results.get(1).getTitle());
    }

}
