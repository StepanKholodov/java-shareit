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

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(RequestHeaders.USER_ID) Long bookerId,
                                          @Valid @RequestBody BookItemRequestDto requestDto) {
        log.info("Create booking {} by booker {}", requestDto, bookerId);
        return bookingClient.create(bookerId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                                           @PathVariable Long bookingId,
                                           @RequestParam boolean approved) {
        log.info("Approve={} booking {} by owner {}", approved, bookingId, ownerId);
        return bookingClient.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                            @PathVariable Long bookingId) {
        log.info("Get booking {} by user {}", bookingId, userId);
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBooker(@RequestHeader(RequestHeaders.USER_ID) Long bookerId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") String stringState) {
        log.info("Get bookings of booker {} with state {}", bookerId, stringState);
        return bookingClient.findAllByBooker(bookerId, stringState);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String stringState) {
        log.info("Get bookings of owner {} with state {}", ownerId, stringState);
        return bookingClient.findAllByOwner(ownerId, stringState);
    }
}
