package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    private ItemRequestClient itemRequestClient;

    @Test
    void create_withValidBody_forwardsToClientAndReturnsResponse() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto("Нужна дрель");
        when(itemRequestClient.create(1L, requestDto))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", 5, "description", "Нужна дрель")));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void findOwn_forwardsToClientAndReturnsResponse() throws Exception {
        when(itemRequestClient.findOwn(1L)).thenReturn(ResponseEntity.ok(List.of(Map.of("id", 5))));

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAllByOthers_forwardsToClientAndReturnsResponse() throws Exception {
        when(itemRequestClient.findAllByOthers(2L)).thenReturn(ResponseEntity.ok(List.of(Map.of("id", 5))));

        mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findById_forwardsToClientAndReturnsResponse() throws Exception {
        when(itemRequestClient.findById(1L, 5L)).thenReturn(ResponseEntity.ok(Map.of("id", 5)));

        mockMvc.perform(get("/requests/5").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void create_withBlankDescription_returns400() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto("   ");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(anyLong(), any());
    }

    @Test
    void create_withDescriptionTooLong_returns400() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto("д".repeat(2001));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(anyLong(), any());
    }
}
