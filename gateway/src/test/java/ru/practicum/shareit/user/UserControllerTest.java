package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
