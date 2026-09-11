package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Marker;

/**
 * Тонкий контроллер: проверяет формат входных данных и проксирует запрос на сервер.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    /**
     * Проверяет формат данных нового пользователя и проксирует запрос на сервер.
     *
     * @param userDto данные пользователя (имя, email); {@code id} игнорируется сервером
     * @return ответ сервера как есть (созданный пользователь либо его ошибка, например 409 при занятом email)
     */
    @PostMapping
    public ResponseEntity<Object> create(@Validated(Marker.OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Create user {}", userDto);
        return userClient.create(userDto);
    }

    /**
     * Проверяет формат переданных полей (не требуя как минимум одного) и проксирует
     * частичное обновление пользователя на сервер.
     *
     * @param userId  id обновляемого пользователя
     * @param userDto новые значения полей; переданное непустое поле должно быть корректным по формату
     * @return ответ сервера как есть (обновлённый пользователь либо его ошибка)
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId,
                                          @Validated(Marker.OnUpdate.class) @RequestBody UserDto userDto) {
        log.info("Update user {} with {}", userId, userDto);
        return userClient.update(userId, userDto);
    }

    /**
     * Проксирует запрос пользователя по id на сервер без собственной валидации.
     *
     * @param userId id пользователя
     * @return ответ сервера как есть (найденный пользователь либо 404)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable Long userId) {
        log.info("Get user {}", userId);
        return userClient.findById(userId);
    }

    /**
     * Проксирует запрос списка всех пользователей на сервер.
     *
     * @return ответ сервера как есть (список пользователей)
     */
    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Get all users");
        return userClient.findAll();
    }

    /**
     * Проксирует удаление пользователя на сервер.
     *
     * @param userId id удаляемого пользователя
     * @return ответ сервера как есть (204 либо 404/409, если пользователь не найден
     *         или с ним связаны другие данные)
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("Delete user {}", userId);
        return userClient.delete(userId);
    }
}
