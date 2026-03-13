package com.blogger.articleManager.repositories;

import com.blogger.articleManager.models.Article;
import com.blogger.articleManager.models.dtos.ArticleSearchCriteria;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> search(ArticleSearchCriteria criteria);
}
