package com.blogger.articleManager.controller;


import com.blogger.articleManager.exceptions.ArticleNotFoundException;
import com.blogger.articleManager.exceptions.InvalidArticleException;
import com.blogger.articleManager.models.dtos.ArticleDTO;
import com.blogger.articleManager.models.dtos.ArticleSearchCriteria;
import com.blogger.articleManager.service.ArticleService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService){
        this.articleService=articleService;
    }

    // Create a new article
    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody ArticleDTO articleDTO) {
        try{
            ArticleDTO createdArticle = articleService.saveArticle(articleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
        }
        catch(InvalidArticleException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Read: Get an article by ID
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable String id) {
        try {
            ArticleDTO article = articleService.getArticleById(new ObjectId(id));
            return ResponseEntity.ok(article);
        } catch (ArticleNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Read: Get all articles
    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        List<ArticleDTO> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    // Update an article by ID
    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable String id, @RequestBody ArticleDTO articleDetails) {
        try {
            ArticleDTO updatedArticle = articleService.updateArticle(new ObjectId(id), articleDetails);
            return ResponseEntity.ok(updatedArticle);
        } catch (ArticleNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidArticleException e) {
            return ResponseEntity.badRequest().body(null); // You may want to return a more informative response
        }
    }

    // Delete an article by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable String id) {
        try {
            boolean deleted = articleService.deleteArticle(new ObjectId(id));
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ArticleNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Unified search: query, author, tags, date range, sort
    @GetMapping("/search")
    public ResponseEntity<List<ArticleDTO>> search(ArticleSearchCriteria criteria) {
        return ResponseEntity.ok(articleService.search(criteria));
    }
}