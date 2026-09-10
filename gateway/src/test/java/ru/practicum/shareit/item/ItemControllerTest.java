package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
