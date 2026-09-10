package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.web.RequestHeaders;

import java.util.Collection;

/**
 * REST-контроллер операций над запросами на вещи.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    /**
     * Создаёт новый запрос на вещь.
     *
     * @param requestorId id пользователя, создающего запрос (из заголовка {@value RequestHeaders#USER_ID})
     * @param requestDto  данные запроса (описание)
     * @return созданный запрос
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader(RequestHeaders.USER_ID) Long requestorId,
                                  @RequestBody ItemRequestDto requestDto) {
        return itemRequestService.create(requestorId, requestDto);
    }

    /**
     * Возвращает собственные запросы текущего пользователя вместе с вещами,
     * добавленными в ответ на них.
     *
     * @param requestorId id пользователя (из заголовка {@value RequestHeaders#USER_ID})
     * @return запросы пользователя, от новых к старым
     */
    @GetMapping
    public Collection<ItemRequestDto> findOwn(@RequestHeader(RequestHeaders.USER_ID) Long requestorId) {
        return itemRequestService.findOwn(requestorId);
    }

    /**
     * Возвращает запросы других пользователей вместе с вещами, добавленными в ответ на них.
     *
     * @param userId id пользователя, выполняющего запрос (из заголовка {@value RequestHeaders#USER_ID})
     * @return запросы других пользователей, от новых к старым
     */
    @GetMapping("/all")
    public Collection<ItemRequestDto> findAllByOthers(@RequestHeader(RequestHeaders.USER_ID) Long userId) {
        return itemRequestService.findAllByOthers(userId);
    }

    /**
     * Возвращает запрос по id вместе с вещами, добавленными в ответ на него.
     *
     * @param userId    id пользователя, выполняющего запрос (из заголовка {@value RequestHeaders#USER_ID})
     * @param requestId id запроса
     * @return найденный запрос
     */
    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                    @PathVariable Long requestId) {
        return itemRequestService.findById(userId, requestId);
    }
}
