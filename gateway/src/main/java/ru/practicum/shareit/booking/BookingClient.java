package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;
import java.util.function.Supplier;

/**
 * REST-клиент для обращения к серверным эндпоинтам {@code /bookings}.
 */
@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory((Supplier<ClientHttpRequestFactory>) HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> create(Long bookerId, BookItemRequestDto requestDto) {
        return post("", bookerId, requestDto);
    }

    public ResponseEntity<Object> approve(Long ownerId, Long bookingId, boolean approved) {
        return patchWithParams("/" + bookingId + "?approved={approved}", ownerId, Map.of("approved", approved));
    }

    public ResponseEntity<Object> findById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllByBooker(Long bookerId, String state) {
        return get("?state={state}", bookerId, Map.of("state", state));
    }

    public ResponseEntity<Object> findAllByOwner(Long ownerId, String state) {
        return get("/owner?state={state}", ownerId, Map.of("state", state));
    }
}
