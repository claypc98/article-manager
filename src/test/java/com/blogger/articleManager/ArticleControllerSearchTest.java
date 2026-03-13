package com.blogger.articleManager;

import com.blogger.articleManager.controller.ArticleController;
import com.blogger.articleManager.models.dtos.ArticleDTO;
import com.blogger.articleManager.models.dtos.ArticleSearchCriteria;
import com.blogger.articleManager.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
class ArticleControllerSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @Test
    void search_withNoParams_returns200WithEmptyArray() throws Exception {
        when(articleService.search(any())).thenReturn(List.of());

        mockMvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void search_withQuery_bindsQueryToCriteria() throws Exception {
        when(articleService.search(any())).thenReturn(List.of());
        var captor = ArgumentCaptor.forClass(ArticleSearchCriteria.class);

        mockMvc.perform(get("/articles/search").param("query", "spring"))
                .andExpect(status().isOk());

        verify(articleService).search(captor.capture());
        assertEquals("spring", captor.getValue().getQuery());
    }

    @Test
    void search_withAuthor_bindsAuthorToCriteria() throws Exception {
        when(articleService.search(any())).thenReturn(List.of());
        var captor = ArgumentCaptor.forClass(ArticleSearchCriteria.class);

        mockMvc.perform(get("/articles/search").param("author", "Alice"))
                .andExpect(status().isOk());

        verify(articleService).search(captor.capture());
        assertEquals("Alice", captor.getValue().getAuthor());
    }

    @Test
    void search_withMultipleTags_bindsTagListToCriteria() throws Exception {
        when(articleService.search(any())).thenReturn(List.of());
        var captor = ArgumentCaptor.forClass(ArticleSearchCriteria.class);

        mockMvc.perform(get("/articles/search")
                        .param("tags", "java")
                        .param("tags", "spring"))
                .andExpect(status().isOk());

        verify(articleService).search(captor.capture());
        assertEquals(List.of("java", "spring"), captor.getValue().getTags());
    }

    @Test
    void search_withSingleTag_bindsTagListToCriteria() throws Exception {
        when(articleService.search(any())).thenReturn(List.of());
        var captor = ArgumentCaptor.forClass(ArticleSearchCriteria.class);

        mockMvc.perform(get("/articles/search").param("tags", "mongodb"))
                .andExpect(status().isOk());

        verify(articleService).search(captor.capture());
        assertEquals(List.of("mongodb"), captor.getValue().getTags());
    }

    @Test
    void search_withDateParams_bindsDatesToCriteria() throws Exception {
        when(articleService.search(any())).thenReturn(List.of());
        var captor = ArgumentCaptor.forClass(ArticleSearchCriteria.class);

        mockMvc.perform(get("/articles/search")
                        .param("createdAfter", "2024-01-01T00:00:00")
                        .param("createdBefore", "2024-12-31T23:59:59"))
                .andExpect(status().isOk());

        verify(articleService).search(captor.capture());
        assertEquals(LocalDateTime.of(2024, 1, 1, 0, 0, 0), captor.getValue().getCreatedAfter());
        assertEquals(LocalDateTime.of(2024, 12, 31, 23, 59, 59), captor.getValue().getCreatedBefore());
    }

    @Test
    void search_withSortParams_bindsSortToCriteria() throws Exception {
        when(articleService.search(any())).thenReturn(List.of());
        var captor = ArgumentCaptor.forClass(ArticleSearchCriteria.class);

        mockMvc.perform(get("/articles/search")
                        .param("sortBy", "title")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk());

        verify(articleService).search(captor.capture());
        assertEquals("title", captor.getValue().getSortBy());
        assertEquals("asc", captor.getValue().getSortDir());
    }

    @Test
    void search_withAllParams_bindsEverythingToCriteria() throws Exception {
        when(articleService.search(any())).thenReturn(List.of());
        var captor = ArgumentCaptor.forClass(ArticleSearchCriteria.class);

        mockMvc.perform(get("/articles/search")
                        .param("query", "microservices")
                        .param("author", "Bob")
                        .param("tags", "java")
                        .param("tags", "spring")
                        .param("createdAfter", "2024-01-01T00:00:00")
                        .param("createdBefore", "2024-06-30T23:59:59")
                        .param("sortBy", "author")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk());

        verify(articleService).search(captor.capture());
        ArticleSearchCriteria bound = captor.getValue();
        assertEquals("microservices", bound.getQuery());
        assertEquals("Bob", bound.getAuthor());
        assertEquals(List.of("java", "spring"), bound.getTags());
        assertNotNull(bound.getCreatedAfter());
        assertNotNull(bound.getCreatedBefore());
        assertEquals("author", bound.getSortBy());
        assertEquals("asc", bound.getSortDir());
    }

    @Test
    void search_returnsJsonRepresentationOfDTOs() throws Exception {
        ArticleDTO dto = new ArticleDTO("id1", "Spring Guide", "Content", "Alice", List.of("java"), LocalDateTime.now());
        when(articleService.search(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Guide"))
                .andExpect(jsonPath("$[0].author").value("Alice"))
                .andExpect(jsonPath("$[0].tags[0]").value("java"));
    }

    @Test
    void search_defaultSortValues_areAppliedWhenNotSpecified() throws Exception {
        when(articleService.search(any())).thenReturn(List.of());
        var captor = ArgumentCaptor.forClass(ArticleSearchCriteria.class);

        mockMvc.perform(get("/articles/search"))
                .andExpect(status().isOk());

        verify(articleService).search(captor.capture());
        assertEquals("createdAt", captor.getValue().getSortBy());
        assertEquals("desc", captor.getValue().getSortDir());
    }
}
