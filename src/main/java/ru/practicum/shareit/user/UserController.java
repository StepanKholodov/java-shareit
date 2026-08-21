package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Marker;

import java.util.Collection;

/**
 * REST-контроллер CRUD-операций над пользователями.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Создаёт нового пользователя.
     *
     * @param userDto данные пользователя (имя, email)
     * @return созданный пользователь с присвоенным id
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated(Marker.OnCreate.class) @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    /**
     * Частично обновляет пользователя: переданы могут быть только изменяемые поля.
     * Поля, которые всё же переданы (например, email), должны быть корректны по формату.
     *
     * @param userId  id обновляемого пользователя
     * @param userDto новые значения полей
     * @return обновлённый пользователь
     */
    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                           @Validated(Marker.OnUpdate.class) @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    /**
     * Возвращает пользователя по id.
     *
     * @param userId id пользователя
     * @return найденный пользователь
     */
    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей
     */
    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    /**
     * Удаляет пользователя.
     *
     * @param userId id удаляемого пользователя
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
