package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.web.RequestHeaders;

/**
 * Тонкий контроллер: проверяет формат входных данных и проксирует запрос на сервер.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    /**
     * Проверяет формат описания запроса и проксирует его создание на сервер.
     *
     * @param requestorId id пользователя, создающего запрос (из заголовка {@value RequestHeaders#USER_ID})
     * @param requestDto  данные запроса (описание)
     * @return ответ сервера как есть (созданный запрос либо его ошибка)
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(RequestHeaders.USER_ID) Long requestorId,
                                          @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Create item request {} by user {}", requestDto, requestorId);
        return itemRequestClient.create(requestorId, requestDto);
    }

    /**
     * Проксирует запрос собственных запросов пользователя на сервер без собственной валидации.
     *
     * @param requestorId id пользователя (из заголовка {@value RequestHeaders#USER_ID})
     * @return ответ сервера как есть (запросы пользователя вместе с ответами на них)
     */
    @GetMapping
    public ResponseEntity<Object> findOwn(@RequestHeader(RequestHeaders.USER_ID) Long requestorId) {
        log.info("Get own item requests of user {}", requestorId);
        return itemRequestClient.findOwn(requestorId);
    }

    /**
     * Проксирует запрос запросов других пользователей на сервер без собственной валидации.
     *
     * @param userId id пользователя, выполняющего запрос (из заголовка {@value RequestHeaders#USER_ID})
     * @return ответ сервера как есть (запросы других пользователей)
     */
    @GetMapping("/all")
    public ResponseEntity<Object> findAllByOthers(@RequestHeader(RequestHeaders.USER_ID) Long userId) {
        log.info("Get item requests of other users for user {}", userId);
        return itemRequestClient.findAllByOthers(userId);
    }

    /**
     * Проксирует запрос запроса по id на сервер без собственной валидации.
     *
     * @param userId    id пользователя, выполняющего запрос (из заголовка {@value RequestHeaders#USER_ID})
     * @param requestId id запроса
     * @return ответ сервера как есть (найденный запрос либо 404)
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                            @PathVariable Long requestId) {
        log.info("Get item request {} for user {}", requestId, userId);
        return itemRequestClient.findById(userId, requestId);
    }
}
