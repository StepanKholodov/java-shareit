package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private UserService userService;

    @Test
    void create_withValidBody_returns201() throws Exception {
        UserDto requestDto = new UserDto(null, "Ivan", "ivan@mail.ru");
        UserDto responseDto = new UserDto(1L, "Ivan", "ivan@mail.ru");
        when(userService.create(any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"));
    }

    @Test
    void create_withBlankName_returns400() throws Exception {
        UserDto requestDto = new UserDto(null, "  ", "ivan@mail.ru");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any());
    }

    @Test
    void create_withInvalidEmail_returns400() throws Exception {
        UserDto requestDto = new UserDto(null, "Ivan", "not-an-email");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any());
    }

    @Test
    void create_withDuplicateEmail_returns409WithErrorBody() throws Exception {
        UserDto requestDto = new UserDto(null, "Ivan", "ivan@mail.ru");
        when(userService.create(any(UserDto.class)))
                .thenThrow(new ConflictException("Пользователь с email ivan@mail.ru уже существует"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Пользователь с email ivan@mail.ru уже существует"));
    }

    @Test
    void update_returns200WithUpdatedUser() throws Exception {
        UserDto requestDto = new UserDto(null, "New name", null);
        UserDto responseDto = new UserDto(1L, "New name", "ivan@mail.ru");
        when(userService.update(eq(1L), any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New name"));
    }

    @Test
    void update_withBlankName_ignoresFieldAndReturns200() throws Exception {
        UserDto requestDto = new UserDto(null, "  ", null);
        UserDto responseDto = new UserDto(1L, "Ivan", "ivan@mail.ru");
        when(userService.update(eq(1L), any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void update_withInvalidEmail_returns400() throws Exception {
        UserDto requestDto = new UserDto(null, null, "not-an-email");

        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(any(), any());
    }

    @Test
    void findById_withNonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Некорректное значение параметра userId"));
    }

    @Test
    void findById_returns200() throws Exception {
        when(userService.findById(1L)).thenReturn(new UserDto(1L, "Ivan", "ivan@mail.ru"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findById_whenNotFound_returns404WithErrorBody() throws Exception {
        when(userService.findById(99L)).thenThrow(new NotFoundException("Пользователь с id 99 не найден"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь с id 99 не найден"));
    }

    @Test
    void findAll_returns200WithList() throws Exception {
        when(userService.findAll()).thenReturn(List.of(
                new UserDto(1L, "Ivan", "ivan@mail.ru"),
                new UserDto(2L, "Petr", "petr@mail.ru")));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(userService).delete(1L);
    }

    @Test
    void delete_whenNotFound_returns404() throws Exception {
        doThrow(new NotFoundException("Пользователь с id 99 не найден"))
                .when(userService).delete(anyLong());

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
    }
}
