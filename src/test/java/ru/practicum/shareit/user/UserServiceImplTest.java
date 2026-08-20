package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Ivan", "ivan@mail.ru");
    }

    @Test
    void getUserById_whenFound_returnsUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getUserById_whenNotFound_throwsNotFoundException() {
        when(userStorage.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_whenEmailFree_savesUser() {
        UserDto inputDto = new UserDto(null, "Ivan", "ivan@mail.ru");
        when(userStorage.existsByEmail("ivan@mail.ru", null)).thenReturn(false);
        when(userStorage.create(any(User.class))).thenReturn(user);

        UserDto result = userService.create(inputDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Ivan");
        assertThat(result.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void create_whenEmailTaken_throwsConflictException() {
        UserDto inputDto = new UserDto(null, "Ivan", "ivan@mail.ru");
        when(userStorage.existsByEmail("ivan@mail.ru", null)).thenReturn(true);

        assertThatThrownBy(() -> userService.create(inputDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ivan@mail.ru");

        verify(userStorage, never()).create(any());
    }

    @Test
    void update_withNewNameAndFreeEmail_updatesBothFields() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(user));
        when(userStorage.existsByEmail("new@mail.ru", 1L)).thenReturn(false);
        when(userStorage.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.update(1L, new UserDto(null, "New name", "new@mail.ru"));

        assertThat(result.getName()).isEqualTo("New name");
        assertThat(result.getEmail()).isEqualTo("new@mail.ru");
    }

    @Test
    void update_whenEmailTakenByAnotherUser_throwsConflictException() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(user));
        when(userStorage.existsByEmail("taken@mail.ru", 1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1L, new UserDto(null, null, "taken@mail.ru")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("taken@mail.ru");

        verify(userStorage, never()).update(any());
    }

    @Test
    void update_withBlankFields_keepsOriginalValues() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(user));
        when(userStorage.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.update(1L, new UserDto(null, "   ", "   "));

        assertThat(result.getName()).isEqualTo("Ivan");
        assertThat(result.getEmail()).isEqualTo("ivan@mail.ru");
        verify(userStorage, never()).existsByEmail(any(), any());
    }

    @Test
    void update_withNullFields_keepsOriginalValues() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(user));
        when(userStorage.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.update(1L, new UserDto(null, null, null));

        assertThat(result.getName()).isEqualTo("Ivan");
        assertThat(result.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void update_whenUserNotFound_throwsNotFoundException() {
        when(userStorage.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, new UserDto(null, "Name", null)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_returnsMappedDto() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Ivan");
        assertThat(result.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void findAll_returnsMappedList() {
        User second = new User(2L, "Petr", "petr@mail.ru");
        when(userStorage.findAll()).thenReturn(List.of(user, second));

        List<UserDto> result = List.copyOf(userService.findAll());

        assertThat(result).hasSize(2)
                .extracting(UserDto::getEmail)
                .containsExactlyInAnyOrder("ivan@mail.ru", "petr@mail.ru");
    }

    @Test
    void findAll_whenEmpty_returnsEmptyCollection() {
        when(userStorage.findAll()).thenReturn(List.of());

        assertThat(userService.findAll()).isEmpty();
    }

    @Test
    void delete_whenUserExists_removesUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userStorage, times(1)).delete(1L);
    }

    @Test
    void delete_whenUserNotFound_throwsAndDoesNotCallStorageDelete() {
        when(userStorage.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99L)).isInstanceOf(NotFoundException.class);

        verify(userStorage, never()).delete(anyLong());
    }
}
