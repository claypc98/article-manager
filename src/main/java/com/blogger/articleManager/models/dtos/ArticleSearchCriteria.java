package com.blogger.articleManager.models.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class

ArticleSearchCriteria {

    /** Full-text query matched against title, content, and author (case-insensitive). */
    private String query;

    /** Exact author name filter. */
    private String author;

    /** Filter articles that contain at least one of the supplied tags. */
    private List<String> tags;

    /** Lower bound on createdAt (inclusive). */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAfter;

    /** Upper bound on createdAt (inclusive). */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdBefore;

    /** Field to sort by. Allowed values: title, author, createdAt. Defaults to createdAt. */
    private String sortBy = "createdAt";

    /** Sort direction: asc or desc. Defaults to desc. */
    private String sortDir = "desc";
}
