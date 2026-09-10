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
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Проверяет через {@link ItemRequestClient}, что запросы к серверным
 * эндпоинтам {@code /requests} строятся правильно. Живёт в одном пакете
 * с {@link BaseClient} ради доступа к полю {@code rest}.
 */
class ItemRequestClientTest {

    private ItemRequestClient itemRequestClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        itemRequestClient = new ItemRequestClient("http://localhost:9090", new RestTemplateBuilder());
        mockServer = MockRestServiceServer.bindTo((RestTemplate) itemRequestClient.rest).build();
    }

    @Test
    void create_sendsPostToRequestsRoot() {
        mockServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\":5}"));

        ResponseEntity<Object> response = itemRequestClient.create(1L, new ItemRequestDto("Нужна дрель"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        mockServer.verify();
    }

    @Test
    void findOwn_sendsGetToRequestsRoot() {
        mockServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.findOwn(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void findAllByOthers_sendsGetToAllPath() {
        mockServer.expect(requestTo("http://localhost:9090/requests/all"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.findAllByOthers(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void findById_sendsGetToRequestPath() {
        mockServer.expect(requestTo("http://localhost:9090/requests/5"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":5}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.findById(1L, 5L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }
}
