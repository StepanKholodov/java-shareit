package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Проверяет через {@link BookingClient}, что запросы к серверным эндпоинтам
 * {@code /bookings} строятся правильно, включая query-параметры в
 * {@code approve}/{@code findAllByBooker}/{@code findAllByOwner}.
 * Живёт в одном пакете с {@link BaseClient} ради доступа к полю {@code rest}.
 */
class BookingClientTest {

    private BookingClient bookingClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        bookingClient = new BookingClient("http://localhost:9090", new RestTemplateBuilder());
        mockServer = MockRestServiceServer.bindTo((RestTemplate) bookingClient.rest).build();
    }

    @Test
    void create_sendsPostToBookingsRoot() {
        mockServer.expect(requestTo("http://localhost:9090/bookings"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1,\"status\":\"WAITING\"}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.create(2L,
                new BookItemRequestDto(10L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void approve_sendsPatchWithApprovedQueryParam() {
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith("http://localhost:9090/bookings/1")))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(queryParam("approved", "true"))
                .andRespond(withSuccess("{\"id\":1,\"status\":\"APPROVED\"}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.approve(1L, 1L, true);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void findById_sendsGetToBookingPath() {
        mockServer.expect(requestTo("http://localhost:9090/bookings/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.findById(2L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void findAllByBooker_sendsGetWithStateQueryParam() {
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith("http://localhost:9090/bookings")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("state", "ALL"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.findAllByBooker(2L, "ALL");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void findAllByOwner_sendsGetToOwnerPathWithStateQueryParam() {
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith("http://localhost:9090/bookings/owner")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("state", "WAITING"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.findAllByOwner(1L, "WAITING");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }
}
