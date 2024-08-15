package com.blogger.articleManager.mapper;

import com.blogger.articleManager.models.Article;
import com.blogger.articleManager.models.dtos.ArticleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArticleMapper {

ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

ArticleDTO articleToArticleDTO(Article article);
Article articleDTOToArticle(ArticleDTO articleDTO);

}
