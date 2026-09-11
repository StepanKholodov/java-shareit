package ru.practicum.shareit.web;

/**
 * Имена HTTP-заголовков, используемых в REST API.
 */
public final class RequestHeaders {

    /**
     * Заголовок с id пользователя, от лица которого выполняется запрос.
     */
    public static final String USER_ID = "X-Sharer-User-Id";

    private RequestHeaders() {
    }
}
