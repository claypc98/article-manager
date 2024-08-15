package com.blogger.articleManager.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "articles")
public class Article {

    @Id
    private ObjectId objectId;

    @NotBlank(message = "Title cannot be blank")
    @Size(min=1,max=255, message = "Title must be between 1 and 255 characters in length")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(min=1, message = "Content must be at least 1 character in length")
    private String content;

    @NotBlank(message = "Author cannot be blank")
    @Size(min=1,max=255, message = "Author must be between 1 and 100 characters in length")
    private String author;

    @NotNull(message = "Tags cannot be null")
    private List<String> tags;

    @NotNull(message = "Creation date cannot be null")
    private LocalDateTime createdAt;
}
