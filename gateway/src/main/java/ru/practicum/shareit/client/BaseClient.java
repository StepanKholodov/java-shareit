package ru.practicum.shareit.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.web.RequestHeaders;

import java.util.Map;

/**
 * Базовый REST-клиент для обращения gateway к серверу. Прокидывает заголовок
 * {@value RequestHeaders#USER_ID} и при ошибке на стороне сервера возвращает
 * клиенту тот же статус и тело ответа, что вернул сервер.
 */
public class BaseClient {

    protected final RestTemplate rest;

    protected BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> parameters) {
        return exchange(HttpMethod.GET, path, userId, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        return exchange(HttpMethod.POST, path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body) {
        return exchange(HttpMethod.PATCH, path, userId, null, body);
    }

    protected ResponseEntity<Object> patchWithParams(String path, Long userId, Map<String, Object> parameters) {
        return exchange(HttpMethod.PATCH, path, userId, parameters, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return exchange(HttpMethod.DELETE, path, userId, null, null);
    }

    private <T> ResponseEntity<Object> exchange(HttpMethod method, String path, Long userId,
                                                 Map<String, Object> parameters, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        try {
            if (parameters != null) {
                return rest.exchange(path, method, requestEntity, Object.class, parameters);
            }
            return rest.exchange(path, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .headers(headersWithoutTransferEncoding(e.getResponseHeaders()))
                    .body(e.getResponseBodyAsByteArray());
        }
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set(RequestHeaders.USER_ID, String.valueOf(userId));
        }
        return headers;
    }

    private HttpHeaders headersWithoutTransferEncoding(HttpHeaders source) {
        HttpHeaders headers = new HttpHeaders();
        if (source != null) {
            headers.addAll(source);
            headers.remove(HttpHeaders.TRANSFER_ENCODING);
        }
        return headers;
    }
}
