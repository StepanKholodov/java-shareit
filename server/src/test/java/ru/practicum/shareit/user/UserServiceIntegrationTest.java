package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Интеграционный тест на реальной (H2) базе: проверяет, что уникальность email
 * реально обеспечивается ограничением БД (а не только приложением), и что
 * основные операции корректно проходят через реальный {@code UserRepository}.
 */
@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void create_savesUserWithGeneratedId() {
        UserDto result = userService.create(new UserDto(null, "Ivan", "usvc-create@mail.ru"));

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Ivan");
        assertThat(result.getEmail()).isEqualTo("usvc-create@mail.ru");
    }

    @Test
    void create_withDuplicateEmail_throwsConflict() {
        userService.create(new UserDto(null, "Ivan", "usvc-duplicate@mail.ru"));

        assertThatThrownBy(() -> userService.create(new UserDto(null, "Petr", "usvc-duplicate@mail.ru")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void update_changesOnlyProvidedFields() {
        UserDto created = userService.create(new UserDto(null, "Ivan", "usvc-update@mail.ru"));

        UserDto updated = userService.update(created.getId(), new UserDto(null, "New name", null));

        assertThat(updated.getName()).isEqualTo("New name");
        assertThat(updated.getEmail()).isEqualTo("usvc-update@mail.ru");
    }

    @Test
    void update_toEmailTakenByAnotherUser_throwsConflict() {
        userService.create(new UserDto(null, "Petr", "usvc-taken@mail.ru"));
        UserDto created = userService.create(new UserDto(null, "Ivan", "usvc-owner@mail.ru"));

        assertThatThrownBy(() -> userService.update(created.getId(), new UserDto(null, null, "usvc-taken@mail.ru")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void findById_returnsSavedUser() {
        UserDto created = userService.create(new UserDto(null, "Ivan", "usvc-find@mail.ru"));

        UserDto result = userService.findById(created.getId());

        assertThat(result.getEmail()).isEqualTo("usvc-find@mail.ru");
    }

    @Test
    void findById_whenMissing_throwsNotFound() {
        assertThatThrownBy(() -> userService.findById(999_999L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAll_includesSavedUsers() {
        UserDto created = userService.create(new UserDto(null, "Ivan", "usvc-findall@mail.ru"));

        List<UserDto> result = List.copyOf(userService.findAll());

        assertThat(result).extracting(UserDto::getId).contains(created.getId());
    }

    @Test
    void delete_removesUser() {
        UserDto created = userService.create(new UserDto(null, "Ivan", "usvc-delete@mail.ru"));

        userService.delete(created.getId());

        assertThatThrownBy(() -> userService.findById(created.getId())).isInstanceOf(NotFoundException.class);
    }
}
