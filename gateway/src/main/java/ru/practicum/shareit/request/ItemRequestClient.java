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

    public ResponseEntity<Object> create(Long requestorId, ItemRequestDto requestDto) {
        return post("", requestorId, requestDto);
    }

    public ResponseEntity<Object> findOwn(Long requestorId) {
        return get("", requestorId);
    }

    public ResponseEntity<Object> findAllByOthers(Long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> findById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
