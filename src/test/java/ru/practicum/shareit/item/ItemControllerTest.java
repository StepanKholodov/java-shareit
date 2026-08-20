package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
    private ItemService itemService;

    @Test
    void create_withValidBody_returns201() throws Exception {
        ItemDto requestDto = new ItemDto(null, "Дрель", "Простая дрель", true);
        ItemDto responseDto = new ItemDto(1L, "Дрель", "Простая дрель", true);
        when(itemService.create(eq(1L), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void create_withBlankName_returns400() throws Exception {
        ItemDto requestDto = new ItemDto(null, "  ", "Простая дрель", true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(anyLong(), any());
    }

    @Test
    void create_withMissingAvailable_returns400() throws Exception {
        ItemDto requestDto = new ItemDto(null, "Дрель", "Простая дрель", null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenOwnerNotFound_returns404WithErrorBody() throws Exception {
        ItemDto requestDto = new ItemDto(null, "Дрель", "Простая дрель", true);
        when(itemService.create(eq(99L), any(ItemDto.class)))
                .thenThrow(new NotFoundException("Пользователь с id 99 не найден"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 99L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь с id 99 не найден"));
    }

    @Test
    void update_returns200WithUpdatedItem() throws Exception {
        ItemDto requestDto = new ItemDto(null, null, null, false);
        ItemDto responseDto = new ItemDto(1L, "Дрель", "Простая дрель", false);
        when(itemService.update(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void update_whenNotOwner_returns403WithErrorBody() throws Exception {
        ItemDto requestDto = new ItemDto(null, "Hack", null, null);
        when(itemService.update(eq(2L), eq(1L), any(ItemDto.class)))
                .thenThrow(new ForbiddenException("Пользователь с id 2 не является владельцем вещи с id 1"));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Пользователь с id 2 не является владельцем вещи с id 1"));
    }

    @Test
    void findById_returns200() throws Exception {
        when(itemService.findById(1L)).thenReturn(new ItemDto(1L, "Дрель", "Простая дрель", true));

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void findById_whenMissing_returns404() throws Exception {
        when(itemService.findById(404L)).thenThrow(new NotFoundException("Вещь с id 404 не найдена"));

        mockMvc.perform(get("/items/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Вещь с id 404 не найдена"));
    }

    @Test
    void findAllByOwner_returns200WithList() throws Exception {
        when(itemService.findAllByOwner(1L)).thenReturn(List.of(
                new ItemDto(1L, "Дрель", "desc", true),
                new ItemDto(2L, "Отвертка", "desc", false)));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void search_returns200WithMatches() throws Exception {
        when(itemService.search("дрель")).thenReturn(List.of(new ItemDto(1L, "Дрель", "desc", true)));

        mockMvc.perform(get("/items/search").param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void search_withNoMatches_returns200WithEmptyList() throws Exception {
        when(itemService.search("шуруповерт")).thenReturn(List.of());

        mockMvc.perform(get("/items/search").param("text", "шуруповерт"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
