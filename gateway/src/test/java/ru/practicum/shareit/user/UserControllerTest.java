package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ErrorHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void create_withValidBody_forwardsToClientAndReturnsResponse() throws Exception {
        UserDto requestDto = new UserDto(null, "Ivan", "ivan@mail.ru");
        when(userClient.create(requestDto))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", 1, "name", "Ivan")));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"));
    }

    @Test
    void update_forwardsToClientAndReturnsResponse() throws Exception {
        UserDto requestDto = new UserDto(null, "New name", null);
        when(userClient.update(1L, requestDto))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1, "name", "New name")));

        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New name"));
    }

    @Test
    void findById_forwardsToClientAndReturnsResponse() throws Exception {
        when(userClient.findById(1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1, "name", "Ivan")));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ivan"));
    }

    @Test
    void findAll_forwardsToClientAndReturnsResponse() throws Exception {
        when(userClient.findAll()).thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1, "name", "Ivan"))));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void delete_forwardsToClientAndReturnsResponse() throws Exception {
        when(userClient.delete(1L)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void create_withBlankName_returns400() throws Exception {
        UserDto requestDto = new UserDto(null, "  ", "ivan@mail.ru");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any());
    }

    @Test
    void create_withInvalidEmail_returns400() throws Exception {
        UserDto requestDto = new UserDto(null, "Ivan", "not-an-email");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any());
    }

    @Test
    void update_withInvalidEmail_returns400() throws Exception {
        UserDto requestDto = new UserDto(null, null, "not-an-email");

        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).update(anyLong(), any());
    }

    @Test
    void create_withNameTooLong_returns400() throws Exception {
        UserDto requestDto = new UserDto(null, "и".repeat(256), "ivan@mail.ru");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any());
    }

    @Test
    void update_withEmailTooLong_returns400() throws Exception {
        UserDto requestDto = new UserDto(null, null, "a".repeat(315) + "@mail.ru");

        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).update(anyLong(), any());
    }
}
