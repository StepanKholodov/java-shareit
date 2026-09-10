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

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                                          @Validated(Marker.OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("Create item {} by owner {}", itemDto, ownerId);
        return itemClient.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                                          @PathVariable Long itemId,
                                          @Validated(Marker.OnUpdate.class) @RequestBody ItemDto itemDto) {
        log.info("Update item {} by owner {} with {}", itemId, ownerId, itemDto);
        return itemClient.update(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable Long itemId) {
        log.info("Get item {}", itemId);
        return itemClient.findById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwner(@RequestHeader(RequestHeaders.USER_ID) Long ownerId) {
        log.info("Get items of owner {}", ownerId);
        return itemClient.findAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Search items by text '{}'", text);
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                              @PathVariable Long itemId,
                                              @Valid @RequestBody CommentDto commentDto) {
        log.info("Add comment to item {} by user {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
