package com.blogger.articleManager.repositories;

import com.blogger.articleManager.models.Article;
import com.blogger.articleManager.models.dtos.ArticleSearchCriteria;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Repository
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("title", "author", "createdAt");

    private final MongoTemplate mongoTemplate;

    public ArticleRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Article> search(ArticleSearchCriteria criteria) {
        List<Criteria> filters = new ArrayList<>();

        // Full-text query: matches title, content, or author (OR)
        if (criteria.getQuery() != null && !criteria.getQuery().isBlank()) {
            String escaped = Pattern.quote(criteria.getQuery().trim());
            filters.add(new Criteria().orOperator(
                    Criteria.where("title").regex(escaped, "i"),
                    Criteria.where("content").regex(escaped, "i"),
                    Criteria.where("author").regex(escaped, "i")
            ));
        }

        // Exact author filter
        if (criteria.getAuthor() != null && !criteria.getAuthor().isBlank()) {
            filters.add(Criteria.where("author").is(criteria.getAuthor().trim()));
        }

        // Tags: article must contain at least one of the supplied tags
        if (criteria.getTags() != null && !criteria.getTags().isEmpty()) {
            filters.add(Criteria.where("tags").in(criteria.getTags()));
        }

        // Date range
        if (criteria.getCreatedAfter() != null) {
            filters.add(Criteria.where("createdAt").gte(criteria.getCreatedAfter()));
        }
        if (criteria.getCreatedBefore() != null) {
            filters.add(Criteria.where("createdAt").lte(criteria.getCreatedBefore()));
        }

        Query query = new Query();
        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }

        // Sort — whitelist field names to prevent injection
        String sortField = ALLOWED_SORT_FIELDS.contains(criteria.getSortBy())
                ? criteria.getSortBy() : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(criteria.getSortDir())
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        query.with(Sort.by(direction, sortField));

        return mongoTemplate.find(query, Article.class);
    }
}
