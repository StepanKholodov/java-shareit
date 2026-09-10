package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

/**
 * REST-клиент для обращения к серверным эндпоинтам {@code /items}.
 */
@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(buildRestTemplate(builder, serverUrl + API_PREFIX));
    }

    public ResponseEntity<Object> create(Long ownerId, ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(Long ownerId, Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> findById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> findAllByOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(String text) {
        return get("/search?text={text}", null, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
