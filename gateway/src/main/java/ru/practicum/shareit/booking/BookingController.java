package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.web.RequestHeaders;

/**
 * Тонкий контроллер: проверяет формат входных данных и проксирует запрос на сервер.
 * Разбор фильтра {@code state} остаётся на стороне сервера, чтобы не дублировать
 * сообщение об ошибке "Unknown state: ...".
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    /**
     * Проверяет формат данных бронирования (в т.ч. что окончание позже начала)
     * и проксирует запрос на сервер.
     *
     * @param bookerId   id пользователя, создающего бронирование (из заголовка {@value RequestHeaders#USER_ID})
     * @param requestDto данные бронирования (вещь, даты)
     * @return ответ сервера как есть (созданное бронирование либо его ошибка, например
     *         404 если вещь не найдена или её пытается забронировать сам владелец)
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(RequestHeaders.USER_ID) Long bookerId,
                                          @Valid @RequestBody BookItemRequestDto requestDto) {
        log.info("Create booking {} by booker {}", requestDto, bookerId);
        return bookingClient.create(bookerId, requestDto);
    }

    /**
     * Проксирует подтверждение или отклонение бронирования на сервер без собственной валидации.
     *
     * @param ownerId   id пользователя, выполняющего запрос (из заголовка {@value RequestHeaders#USER_ID})
     * @param bookingId id бронирования
     * @param approved  {@code true} — подтвердить, {@code false} — отклонить
     * @return ответ сервера как есть (бронирование с обновлённым статусом либо его ошибка)
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                                           @PathVariable Long bookingId,
                                           @RequestParam boolean approved) {
        log.info("Approve={} booking {} by owner {}", approved, bookingId, ownerId);
        return bookingClient.approve(ownerId, bookingId, approved);
    }

    /**
     * Проксирует запрос бронирования по id на сервер без собственной валидации.
     *
     * @param userId    id пользователя, выполняющего запрос (из заголовка {@value RequestHeaders#USER_ID})
     * @param bookingId id бронирования
     * @return ответ сервера как есть (найденное бронирование либо 404)
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                            @PathVariable Long bookingId) {
        log.info("Get booking {} by user {}", bookingId, userId);
        return bookingClient.findById(userId, bookingId);
    }

    /**
     * Проксирует запрос бронирований текущего пользователя как арендатора на сервер.
     * Сам фильтр {@code state} не разбирает — это остаётся на сервере, чтобы не
     * дублировать сообщение об ошибке "Unknown state: ...".
     *
     * @param bookerId    id пользователя (из заголовка {@value RequestHeaders#USER_ID})
     * @param stringState фильтр состояния ({@code ALL} по умолчанию)
     * @return ответ сервера как есть (бронирования либо 400 при неизвестном состоянии)
     */
    @GetMapping
    public ResponseEntity<Object> findAllByBooker(@RequestHeader(RequestHeaders.USER_ID) Long bookerId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") String stringState) {
        log.info("Get bookings of booker {} with state {}", bookerId, stringState);
        return bookingClient.findAllByBooker(bookerId, stringState);
    }

    /**
     * Проксирует запрос бронирований всех вещей текущего пользователя-владельца на сервер.
     *
     * @param ownerId     id пользователя (из заголовка {@value RequestHeaders#USER_ID})
     * @param stringState фильтр состояния ({@code ALL} по умолчанию)
     * @return ответ сервера как есть (бронирования либо 400 при неизвестном состоянии)
     */
    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String stringState) {
        log.info("Get bookings of owner {} with state {}", ownerId, stringState);
        return bookingClient.findAllByOwner(ownerId, stringState);
    }
}
