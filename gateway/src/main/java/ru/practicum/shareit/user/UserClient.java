package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * REST-клиент для обращения к серверным эндпоинтам {@code /users}.
 */
@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(buildRestTemplate(builder, serverUrl + API_PREFIX));
    }

    /**
     * @param userDto данные создаваемого пользователя
     * @return ответ сервера как есть ({@code POST /users})
     */
    public ResponseEntity<Object> create(UserDto userDto) {
        return post("", null, userDto);
    }

    /**
     * @param userId  id обновляемого пользователя
     * @param userDto новые значения полей
     * @return ответ сервера как есть ({@code PATCH /users/{userId}})
     */
    public ResponseEntity<Object> update(Long userId, UserDto userDto) {
        return patch("/" + userId, null, userDto);
    }

    /**
     * @param userId id пользователя
     * @return ответ сервера как есть ({@code GET /users/{userId}})
     */
    public ResponseEntity<Object> findById(Long userId) {
        return get("/" + userId);
    }

    /**
     * @return ответ сервера как есть ({@code GET /users})
     */
    public ResponseEntity<Object> findAll() {
        return get("");
    }

    /**
     * @param userId id удаляемого пользователя
     * @return ответ сервера как есть ({@code DELETE /users/{userId}})
     */
    public ResponseEntity<Object> delete(Long userId) {
        return delete("/" + userId, null);
    }
}
