package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.web.RequestHeaders;

import java.util.Collection;

/**
 * REST-контроллер операций над бронированиями.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Создаёт запрос на бронирование вещи.
     *
     * @param bookerId   id пользователя, создающего бронирование (из заголовка {@value RequestHeaders#USER_ID})
     * @param requestDto данные бронирования (вещь, даты)
     * @return созданное бронирование в статусе {@code WAITING}
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader(RequestHeaders.USER_ID) Long bookerId,
                              @RequestBody BookItemRequestDto requestDto) {
        return bookingService.create(bookerId, requestDto);
    }

    /**
     * Подтверждает или отклоняет запрос на бронирование. Доступно только владельцу вещи.
     *
     * @param ownerId   id пользователя, выполняющего запрос (из заголовка {@value RequestHeaders#USER_ID})
     * @param bookingId id бронирования
     * @param approved  {@code true} — подтвердить, {@code false} — отклонить
     * @return бронирование с обновлённым статусом
     */
    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                               @PathVariable Long bookingId,
                               @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    /**
     * Возвращает бронирование по id. Доступно только автору бронирования или владельцу вещи.
     *
     * @param userId    id пользователя, выполняющего запрос (из заголовка {@value RequestHeaders#USER_ID})
     * @param bookingId id бронирования
     * @return найденное бронирование
     */
    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(RequestHeaders.USER_ID) Long userId, @PathVariable Long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    /**
     * Возвращает бронирования текущего пользователя как арендатора.
     *
     * @param bookerId    id пользователя (из заголовка {@value RequestHeaders#USER_ID})
     * @param stringState фильтр состояния ({@code ALL} по умолчанию)
     * @return бронирования, отсортированные от новых к старым
     */
    @GetMapping
    public Collection<BookingDto> findAllByBooker(@RequestHeader(RequestHeaders.USER_ID) Long bookerId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") String stringState) {
        return bookingService.findAllByBooker(bookerId, parseState(stringState));
    }

    /**
     * Возвращает бронирования для всех вещей текущего пользователя-владельца.
     *
     * @param ownerId     id пользователя (из заголовка {@value RequestHeaders#USER_ID})
     * @param stringState фильтр состояния ({@code ALL} по умолчанию)
     * @return бронирования, отсортированные от новых к старым
     */
    @GetMapping("/owner")
    public Collection<BookingDto> findAllByOwner(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String stringState) {
        return bookingService.findAllByOwner(ownerId, parseState(stringState));
    }

    private BookingState parseState(String stringState) {
        return BookingState.from(stringState)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stringState));
    }
}
