package ru.practicum.shareit.exception;

import lombok.Getter;

/**
 * Единый формат тела ответа для всех ошибок API: {@code {"error": "текст сообщения"}}.
 */
@Getter
public class ErrorResponse {

    private final String error;

    /**
     * @param error текст сообщения об ошибке
     */
    public ErrorResponse(String error) {
        this.error = error;
    }
}
