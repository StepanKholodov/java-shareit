package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Marker;
import ru.practicum.shareit.web.RequestHeaders;

/**
 * Тонкий контроллер: проверяет формат входных данных и проксирует запрос на сервер.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    /**
     * Проверяет формат данных новой вещи и проксирует запрос на сервер.
     *
     * @param ownerId id владельца (из заголовка {@value RequestHeaders#USER_ID})
     * @param itemDto данные вещи (название, описание, доступность, опционально {@code requestId});
     *                {@code id} игнорируется сервером
     * @return ответ сервера как есть (созданная вещь либо её ошибка)
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                                          @Validated(Marker.OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("Create item {} by owner {}", itemDto, ownerId);
        return itemClient.create(ownerId, itemDto);
    }

    /**
     * Проверяет формат переданных полей (не требуя как минимум одного) и проксирует
     * частичное обновление вещи на сервер.
     *
     * @param ownerId id пользователя, выполняющего запрос (из заголовка {@value RequestHeaders#USER_ID})
     * @param itemId  id обновляемой вещи
     * @param itemDto новые значения полей; переданное непустое поле должно быть корректным по формату
     * @return ответ сервера как есть (обновлённая вещь либо её ошибка, например 403 не у владельца)
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                                          @PathVariable Long itemId,
                                          @Validated(Marker.OnUpdate.class) @RequestBody ItemDto itemDto) {
        log.info("Update item {} by owner {} with {}", itemId, ownerId, itemDto);
        return itemClient.update(ownerId, itemId, itemDto);
    }

    /**
     * Проксирует запрос вещи по id на сервер без собственной валидации.
     *
     * @param itemId id вещи
     * @return ответ сервера как есть (найденная вещь либо 404)
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable Long itemId) {
        log.info("Get item {}", itemId);
        return itemClient.findById(itemId);
    }

    /**
     * Проксирует запрос списка вещей владельца на сервер.
     *
     * @param ownerId id владельца (из заголовка {@value RequestHeaders#USER_ID})
     * @return ответ сервера как есть (список вещей владельца)
     */
    @GetMapping
    public ResponseEntity<Object> findAllByOwner(@RequestHeader(RequestHeaders.USER_ID) Long ownerId) {
        log.info("Get items of owner {}", ownerId);
        return itemClient.findAllByOwner(ownerId);
    }

    /**
     * Проверяет наличие обязательного параметра {@code text} и проксирует поиск на сервер.
     *
     * @param text текст для поиска
     * @return ответ сервера как есть (найденные доступные вещи)
     */
    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Search items by text '{}'", text);
        return itemClient.search(text);
    }

    /**
     * Проверяет формат текста отзыва и проксирует его добавление на сервер.
     *
     * @param userId     id автора отзыва (из заголовка {@value RequestHeaders#USER_ID})
     * @param itemId     id вещи
     * @param commentDto текст отзыва
     * @return ответ сервера как есть (созданный отзыв либо 400, если аренда не завершена)
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                              @PathVariable Long itemId,
                                              @Valid @RequestBody CommentDto commentDto) {
        log.info("Add comment to item {} by user {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
