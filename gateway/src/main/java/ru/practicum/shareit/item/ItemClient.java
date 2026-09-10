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

    /**
     * @param ownerId id владельца, которому будет принадлежать вещь
     * @param itemDto данные вещи
     * @return ответ сервера как есть ({@code POST /items})
     */
    public ResponseEntity<Object> create(Long ownerId, ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    /**
     * @param ownerId id пользователя, выполняющего запрос
     * @param itemId  id обновляемой вещи
     * @param itemDto новые значения полей
     * @return ответ сервера как есть ({@code PATCH /items/{itemId}})
     */
    public ResponseEntity<Object> update(Long ownerId, Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, ownerId, itemDto);
    }

    /**
     * @param itemId id вещи
     * @return ответ сервера как есть ({@code GET /items/{itemId}})
     */
    public ResponseEntity<Object> findById(Long itemId) {
        return get("/" + itemId);
    }

    /**
     * @param ownerId id владельца
     * @return ответ сервера как есть ({@code GET /items})
     */
    public ResponseEntity<Object> findAllByOwner(Long ownerId) {
        return get("", ownerId);
    }

    /**
     * @param text текст для поиска
     * @return ответ сервера как есть ({@code GET /items/search?text=...})
     */
    public ResponseEntity<Object> search(String text) {
        return get("/search?text={text}", null, Map.of("text", text));
    }

    /**
     * @param userId     id автора отзыва
     * @param itemId     id вещи
     * @param commentDto текст отзыва
     * @return ответ сервера как есть ({@code POST /items/{itemId}/comment})
     */
    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
