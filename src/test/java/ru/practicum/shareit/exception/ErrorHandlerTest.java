package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFound_returnsExceptionMessage() {
        ErrorResponse response = errorHandler.handleNotFound(new NotFoundException("не найдено"));

        assertThat(response.getError()).isEqualTo("не найдено");
    }

    @Test
    void handleForbidden_returnsExceptionMessage() {
        ErrorResponse response = errorHandler.handleForbidden(new ForbiddenException("нет доступа"));

        assertThat(response.getError()).isEqualTo("нет доступа");
    }

    @Test
    void handleConflict_returnsExceptionMessage() {
        ErrorResponse response = errorHandler.handleConflict(new ConflictException("уже существует"));

        assertThat(response.getError()).isEqualTo("уже существует");
    }

    @Test
    void handleMethodArgumentNotValid_withFieldError_returnsFieldErrorMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("itemDto", "name", "Название не может быть пустым");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(fieldError);

        ErrorResponse response = errorHandler.handleMethodArgumentNotValid(ex);

        assertThat(response.getError()).isEqualTo("Название не может быть пустым");
    }

    @Test
    void handleMethodArgumentNotValid_withoutFieldError_returnsDefaultMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(null);

        ErrorResponse response = errorHandler.handleMethodArgumentNotValid(ex);

        assertThat(response.getError()).isEqualTo("Ошибка валидации");
    }

    @Test
    void handleMissingHeader_returnsMessageWithHeaderName() {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("X-Sharer-User-Id");

        ErrorResponse response = errorHandler.handleMissingHeader(ex);

        assertThat(response.getError()).isEqualTo("Отсутствует обязательный заголовок X-Sharer-User-Id");
    }

    @Test
    void handleMissingParameter_returnsMessageWithParameterName() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("text", "String");

        ErrorResponse response = errorHandler.handleMissingParameter(ex);

        assertThat(response.getError()).isEqualTo("Отсутствует обязательный параметр text");
    }

    @Test
    void handleMessageNotReadable_returnsGenericMessage() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ErrorResponse response = errorHandler.handleMessageNotReadable(ex);

        assertThat(response.getError()).isEqualTo("Некорректное тело запроса");
    }
}
