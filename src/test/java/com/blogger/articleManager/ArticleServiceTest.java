package com.blogger.articleManager;

import com.blogger.articleManager.models.Article;
import com.blogger.articleManager.models.dtos.ArticleDTO;
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

}
