package com.blogger.articleManager.models.dtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor
@Data
public class ArticleDTO {
    @NotEmpty(message = "The ID cannot be empty")
    private String id;

    @NotBlank(message = "The title of the article is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotBlank(message = "The content for the article cannot be empty")
    @Size(min = 1, message = "Content must be at least 1 character long")
    private String content;

    @NotBlank(message = "The author for the article is required")
    @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
    private String author;

    private List<@Size(min = 1, max = 30, message = "Each tag must be between 1 and 30 characters") String> tags;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

}
