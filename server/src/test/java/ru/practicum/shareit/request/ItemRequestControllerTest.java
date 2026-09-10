package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@Import(ErrorHandler.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto sampleRequestDto() {
        return new ItemRequestDto(5L, "Нужна дрель", LocalDateTime.now(),
                List.of(new ItemRequestItemDto(10L, "Дрель", 2L)));
    }

    @Test
    void create_withValidBody_returns201() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(null, "Нужна дрель", null, null);
        when(itemRequestService.create(eq(1L), any(ItemRequestDto.class))).thenReturn(sampleRequestDto());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void create_whenRequestorNotFound_returns404WithErrorBody() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(null, "Нужна дрель", null, null);
        when(itemRequestService.create(eq(99L), any(ItemRequestDto.class)))
                .thenThrow(new NotFoundException("Пользователь с id 99 не найден"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 99L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь с id 99 не найден"));
    }

    @Test
    void findOwn_returns200WithList() throws Exception {
        when(itemRequestService.findOwn(1L)).thenReturn(List.of(sampleRequestDto()));

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].name").value("Дрель"));
    }

    @Test
    void findAllByOthers_returns200WithList() throws Exception {
        when(itemRequestService.findAllByOthers(2L)).thenReturn(List.of(sampleRequestDto()));

        mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findById_returns200() throws Exception {
        when(itemRequestService.findById(1L, 5L)).thenReturn(sampleRequestDto());

        mockMvc.perform(get("/requests/5").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.items[0].ownerId").value(2));
    }

    @Test
    void findById_whenMissing_returns404() throws Exception {
        when(itemRequestService.findById(1L, 404L))
                .thenThrow(new NotFoundException("Запрос с id 404 не найден"));

        mockMvc.perform(get("/requests/404").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Запрос с id 404 не найден"));
    }
}
