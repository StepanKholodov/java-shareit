package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

/**
 * REST-клиент для обращения к серверным эндпоинтам {@code /bookings}.
 */
@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(buildRestTemplate(builder, serverUrl + API_PREFIX));
    }

    /**
     * @param bookerId   id пользователя, создающего бронирование
     * @param requestDto данные бронирования (вещь, даты)
     * @return ответ сервера как есть ({@code POST /bookings})
     */
    public ResponseEntity<Object> create(Long bookerId, BookItemRequestDto requestDto) {
        return post("", bookerId, requestDto);
    }

    /**
     * @param ownerId   id пользователя, выполняющего запрос
     * @param bookingId id бронирования
     * @param approved  {@code true} — подтвердить, {@code false} — отклонить
     * @return ответ сервера как есть ({@code PATCH /bookings/{bookingId}?approved=...})
     */
    public ResponseEntity<Object> approve(Long ownerId, Long bookingId, boolean approved) {
        return patchWithParams("/" + bookingId + "?approved={approved}", ownerId, Map.of("approved", approved));
    }

    /**
     * @param userId    id пользователя, выполняющего запрос
     * @param bookingId id бронирования
     * @return ответ сервера как есть ({@code GET /bookings/{bookingId}})
     */
    public ResponseEntity<Object> findById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    /**
     * @param bookerId id пользователя-арендатора
     * @param state    фильтр состояния как есть (не разбирается на gateway)
     * @return ответ сервера как есть ({@code GET /bookings?state=...})
     */
    public ResponseEntity<Object> findAllByBooker(Long bookerId, String state) {
        return get("?state={state}", bookerId, Map.of("state", state));
    }

    /**
     * @param ownerId id пользователя-владельца
     * @param state   фильтр состояния как есть (не разбирается на gateway)
     * @return ответ сервера как есть ({@code GET /bookings/owner?state=...})
     */
    public ResponseEntity<Object> findAllByOwner(Long ownerId, String state) {
        return get("/owner?state={state}", ownerId, Map.of("state", state));
    }
}
