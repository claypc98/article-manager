package com.blogger.articleManager.service;


import com.blogger.articleManager.exceptions.ArticleNotFoundException;
import com.blogger.articleManager.exceptions.InvalidArticleException;
import com.blogger.articleManager.mapper.ArticleMapper;
import com.blogger.articleManager.models.Article;
import com.blogger.articleManager.models.dtos.ArticleDTO;
import com.blogger.articleManager.repositories.ArticleRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository){
        this.articleRepository=articleRepository;
    }

    private static final ArticleMapper articleMapper = ArticleMapper.INSTANCE;
    private static final String ARTICLE_NOT_FOUND_EXCEPTION_MESSAGE = "Article not found with ID: ";
    public ArticleDTO saveArticle(ArticleDTO articleDTO){

        validateArticle(articleDTO);
        Article article = articleMapper.articleDTOToArticle(articleDTO);
        Article savedArticle = articleRepository.save(article);
        return articleMapper.articleToArticleDTO(savedArticle);
    }

    public ArticleDTO getArticleById(ObjectId id){
        Article article = articleRepository.findByObjectId(id)
                .orElseThrow(()-> new ArticleNotFoundException(ARTICLE_NOT_FOUND_EXCEPTION_MESSAGE +id));

        return articleMapper.articleToArticleDTO(article);
    }

    public List<ArticleDTO> getAllArticles(){
        return articleRepository.findAll().stream()
                .map(articleMapper::articleToArticleDTO).collect(Collectors.toList());
    }

    public ArticleDTO updateArticle(ObjectId id, ArticleDTO articleDetails) {
        Optional<Article> existingArticleOptional = articleRepository.findByObjectId(id);

        if(existingArticleOptional.isEmpty()){
            throw new ArticleNotFoundException(ARTICLE_NOT_FOUND_EXCEPTION_MESSAGE+id);
        }

        Article existingArticle = existingArticleOptional.get();

            existingArticle.setTitle(articleDetails.getTitle());
            existingArticle.setContent(articleDetails.getContent());
            existingArticle.setAuthor(articleDetails.getAuthor());
            existingArticle.setTags(articleDetails.getTags());
            Article updatedArticle = articleRepository.save(existingArticle);

        return articleMapper.articleToArticleDTO(updatedArticle);
    }

    public boolean deleteArticle(ObjectId id) {
        if (articleRepository.existsById(id)) {
            articleRepository.deleteById(id);
            return true;
        } else {
            throw new ArticleNotFoundException(ARTICLE_NOT_FOUND_EXCEPTION_MESSAGE+ id);
        }
    }

    public List<ArticleDTO> getArticlesByAuthor(String author) {
        List<Article> articles = articleRepository.findByAuthor(author);
        return articles.stream().map(articleMapper::articleToArticleDTO).collect(Collectors.toList());
    }

    public List<ArticleDTO> searchArticlesByKeyword(String keyword) {
        List<Article> articles = articleRepository.findByTitleContaining(keyword);
        return articles.stream().map(articleMapper::articleToArticleDTO).collect(Collectors.toList());
    }

    public List<ArticleDTO> getRecentArticles(LocalDateTime date) {
        List<Article> articles = articleRepository.findByCreatedAtAfter(date);
        return articles.stream().map(articleMapper::articleToArticleDTO).collect(Collectors.toList());
    }

    public List<ArticleDTO> getArticlesByAuthorSorted(String author) {
        List<Article> articles = articleRepository.findByAuthorSortedByCreatedAtDesc(author, Sort.by(Sort.Direction.DESC, "createdAt"));
        return articles.stream().map(articleMapper::articleToArticleDTO).collect(Collectors.toList());
    }

    public List<ArticleDTO> getArticlesByTag(String tag) {
        List<Article> articles = articleRepository.findByTag(tag);
        return articles.stream().map(articleMapper::articleToArticleDTO).collect(Collectors.toList());
    }

    public List<ArticleDTO> searchArticlesByKeywordAndTag(String keyword, String tag) {
        List<Article> articles = articleRepository.findByTitleContainingAndTag(keyword, tag);
        return articles.stream().map(articleMapper::articleToArticleDTO).collect(Collectors.toList());
    }

    // Additional methods to cover all repository methods

    public ArticleDTO getArticleByObjectId(ObjectId id) {
        Article article = articleRepository.findByObjectId(id)
                .orElseThrow(() -> new ArticleNotFoundException(ARTICLE_NOT_FOUND_EXCEPTION_MESSAGE+ id));
        return articleMapper.articleToArticleDTO(article);
    }

    public List<ArticleDTO> getAllArticlesSortedByCreatedAt() {
        List<Article> articles = articleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return articles.stream().map(articleMapper::articleToArticleDTO).collect(Collectors.toList());
    }

    private void validateArticle(ArticleDTO articleDTO){
        if(articleDTO.getTitle()==null||articleDTO.getTitle().isEmpty()){
            throw new InvalidArticleException("Article title cannot be empty");
        }
    }

}
