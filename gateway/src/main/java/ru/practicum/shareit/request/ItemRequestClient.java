package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

/**
 * REST-клиент для обращения к серверным эндпоинтам {@code /requests}.
 */
@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(buildRestTemplate(builder, serverUrl + API_PREFIX));
    }

    /**
     * @param requestorId id пользователя, создающего запрос
     * @param requestDto  данные запроса (описание)
     * @return ответ сервера как есть ({@code POST /requests})
     */
    public ResponseEntity<Object> create(Long requestorId, ItemRequestDto requestDto) {
        return post("", requestorId, requestDto);
    }

    /**
     * @param requestorId id пользователя
     * @return ответ сервера как есть ({@code GET /requests})
     */
    public ResponseEntity<Object> findOwn(Long requestorId) {
        return get("", requestorId);
    }

    /**
     * @param userId id пользователя, выполняющего запрос
     * @return ответ сервера как есть ({@code GET /requests/all})
     */
    public ResponseEntity<Object> findAllByOthers(Long userId) {
        return get("/all", userId);
    }

    /**
     * @param userId    id пользователя, выполняющего запрос
     * @param requestId id запроса
     * @return ответ сервера как есть ({@code GET /requests/{requestId}})
     */
    public ResponseEntity<Object> findById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
