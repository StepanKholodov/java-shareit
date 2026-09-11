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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Проверяет через {@link ItemClient}, что запросы к серверным эндпоинтам
 * {@code /items} строятся правильно, включая query-параметр в {@code search}.
 * Живёт в одном пакете с {@link BaseClient} ради доступа к полю {@code rest}.
 */
class ItemClientTest {

    private ItemClient itemClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        itemClient = new ItemClient("http://localhost:9090", new RestTemplateBuilder());
        mockServer = MockRestServiceServer.bindTo((RestTemplate) itemClient.rest).build();
    }

    @Test
    void create_sendsPostToItemsRoot() {
        mockServer.expect(requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\":1,\"name\":\"Дрель\"}"));

        ResponseEntity<Object> response = itemClient.create(1L, new ItemDto(null, "Дрель", "desc", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        mockServer.verify();
    }

    @Test
    void update_sendsPatchToItemPath() {
        mockServer.expect(requestTo("http://localhost:9090/items/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.update(1L, 1L, new ItemDto(null, null, null, false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void findById_sendsGetToItemPath() {
        mockServer.expect(requestTo("http://localhost:9090/items/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void findAllByOwner_sendsGetToItemsRoot() {
        mockServer.expect(requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.findAllByOwner(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void search_sendsGetWithTextQueryParam() {
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith("http://localhost:9090/items/search")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("text", "drill"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.search("drill");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void addComment_sendsPostToCommentPath() {
        mockServer.expect(requestTo("http://localhost:9090/items/1/comment"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\":1}"));

        ResponseEntity<Object> response = itemClient.addComment(2L, 1L, new CommentDto(null, "Отлично"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        mockServer.verify();
    }
}
