package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Остальные обработчики {@link ErrorHandler} (валидация, отсутствующий заголовок/параметр,
 * нечитаемое тело, несовпадение типа) уже покрыты MockMvc-тестами контроллеров через
 * {@code @Import(ErrorHandler.class)} — здесь только два добавленных как защитная сетка.
 */
class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleMethodNotSupported_returnsMessageWithMethodName() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException(HttpMethod.PUT.name());

        ErrorResponse response = errorHandler.handleMethodNotSupported(ex);

        assertThat(response.getError()).isEqualTo("Метод PUT не поддерживается для этого пути");
    }

    @Test
    void handleUnexpected_returnsGenericMessage() {
        ErrorResponse response = errorHandler.handleUnexpected(new IllegalStateException("что-то пошло не так"));

        assertThat(response.getError()).isEqualTo("Внутренняя ошибка сервера");
    }
}
