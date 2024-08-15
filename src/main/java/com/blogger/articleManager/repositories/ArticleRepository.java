package com.blogger.articleManager.repositories;

import com.blogger.articleManager.models.Article;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends MongoRepository<Article, ObjectId> {

    /**
     * Find an article by its ObjectId.
     *
     * @param id the ObjectId of the article
     * @return an Optional containing the found article or empty if not found
     */
    Optional<Article> findByObjectId(ObjectId id);

    /**
     * Find all articles written by a specific author.
     *
     * @param author the author's name
     * @return a list of articles written by the specified author
     */
    List<Article> findByAuthor(String author);

    /**
     * Find all articles containing a specific keyword in the title.
     *
     * @param keyword the keyword to search in the title
     * @return a list of articles with titles containing the keyword
     */
    List<Article> findByTitleContaining(String keyword);

    /**
     * Find all articles created after a specific date.
     *
     * @param date the date to compare against
     * @return a list of articles created after the specified date
     */
    List<Article> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find all articles by a specific author, sorted by creation date in descending order.
     *
     * @param author the author's name
     * @param sort the sorting criteria
     * @return a list of articles written by the specified author, sorted by creation date
     */
    @Query("{'author': ?0}")
    List<Article> findByAuthorSortedByCreatedAtDesc(String author, Sort sort);

    /**
     * Find all articles that contain a specific tag.
     *
     * @param tag the tag to search for
     * @return a list of articles containing the specified tag
     */
    @Query("{ 'tags': ?0 }")
    List<Article> findByTag(String tag);

    /**
     * Find all articles that contain a specific keyword in the title and a specific tag.
     *
     * @param keyword the keyword to search in the title
     * @param tag the tag to search for
     * @return a list of articles containing the keyword in the title and the specified tag
     */
    @Query("{ 'title': { $regex: ?0, $options: 'i' }, 'tags': ?1 }")
    List<Article> findByTitleContainingAndTag(String keyword, String tag);
}
