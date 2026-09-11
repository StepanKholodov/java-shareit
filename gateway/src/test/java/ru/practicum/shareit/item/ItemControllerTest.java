package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import(ErrorHandler.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void create_withValidBody_forwardsToClientAndReturnsResponse() throws Exception {
        ItemDto requestDto = new ItemDto(null, "Дрель", "Простая дрель", true);
        when(itemClient.create(1L, requestDto))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", 1, "name", "Дрель")));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void update_forwardsToClientAndReturnsResponse() throws Exception {
        ItemDto requestDto = new ItemDto(null, null, null, false);
        when(itemClient.update(1L, 1L, requestDto))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1, "available", false)));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void findById_forwardsToClientAndReturnsResponse() throws Exception {
        when(itemClient.findById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1, "name", "Дрель")));

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void findAllByOwner_forwardsToClientAndReturnsResponse() throws Exception {
        when(itemClient.findAllByOwner(1L))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1, "name", "Дрель"))));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void search_forwardsToClientAndReturnsResponse() throws Exception {
        when(itemClient.search("дрель"))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1, "name", "Дрель"))));

        mockMvc.perform(get("/items/search").param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void addComment_withValidBody_forwardsToClientAndReturnsResponse() throws Exception {
        CommentDto requestDto = new CommentDto(null, "Отличная дрель");
        when(itemClient.addComment(2L, 1L, requestDto))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", 1, "text", "Отличная дрель")));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Отличная дрель"));
    }

    @Test
    void create_withBlankName_returns400() throws Exception {
        ItemDto requestDto = new ItemDto(null, "  ", "Простая дрель", true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void create_withMissingAvailable_returns400() throws Exception {
        ItemDto requestDto = new ItemDto(null, "Дрель", "Простая дрель", null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void addComment_withBlankText_returns400() throws Exception {
        CommentDto requestDto = new CommentDto(null, "   ");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any());
    }

    @Test
    void create_withNameTooLong_returns400() throws Exception {
        ItemDto requestDto = new ItemDto(null, "д".repeat(256), "Простая дрель", true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void update_withDescriptionTooLong_returns400() throws Exception {
        ItemDto requestDto = new ItemDto(null, null, "д".repeat(2001), null);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).update(anyLong(), anyLong(), any());
    }

    @Test
    void update_withEmptyName_returns400() throws Exception {
        ItemDto requestDto = new ItemDto(null, "", null, null);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).update(anyLong(), anyLong(), any());
    }

    @Test
    void update_withEmptyDescription_returns400() throws Exception {
        ItemDto requestDto = new ItemDto(null, null, "", null);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).update(anyLong(), anyLong(), any());
    }

    @Test
    void addComment_withTextTooLong_returns400() throws Exception {
        CommentDto requestDto = new CommentDto(null, "д".repeat(2001));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any());
    }
}
