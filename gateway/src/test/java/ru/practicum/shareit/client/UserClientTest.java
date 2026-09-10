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
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Проверяет через {@link UserClient}, что {@code BaseClient} строит правильные
 * запросы к серверу и прозрачно прокидывает как успешный, так и ошибочный ответ.
 * Живёт в одном пакете с {@link BaseClient}, чтобы иметь доступ к его
 * защищённому полю {@code rest} и подменить его на {@link MockRestServiceServer}.
 */
class UserClientTest {

    private UserClient userClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        userClient = new UserClient("http://localhost:9090", new RestTemplateBuilder());
        mockServer = MockRestServiceServer.bindTo((RestTemplate) userClient.rest).build();
    }

    @Test
    void create_sendsPostAndReturnsBody() {
        mockServer.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\":1,\"name\":\"Ivan\"}"));

        ResponseEntity<Object> response = userClient.create(new UserDto(null, "Ivan", "ivan@mail.ru"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        mockServer.verify();
    }

    @Test
    void update_sendsPatchToUserPath() {
        mockServer.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess("{\"id\":1,\"name\":\"New name\"}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.update(1L, new UserDto(null, "New name", null));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void findById_sendsGetToUserPath() {
        mockServer.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void findAll_sendsGetToUsersRoot() {
        mockServer.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void delete_sendsDeleteToUserPath() {
        mockServer.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        ResponseEntity<Object> response = userClient.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        mockServer.verify();
    }

    @Test
    void findById_whenServerUnreachable_returns503() {
        UserClient unreachableClient = new UserClient("http://localhost:1", new RestTemplateBuilder());

        ResponseEntity<Object> response = unreachableClient.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void findById_whenServerReturnsError_passesThroughStatusAndBody() {
        mockServer.expect(requestTo("http://localhost:9090/users/404"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"Пользователь с id 404 не найден\"}"));

        ResponseEntity<Object> response = userClient.findById(404L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(new String((byte[]) response.getBody())).contains("не найден");
        mockServer.verify();
    }
}
