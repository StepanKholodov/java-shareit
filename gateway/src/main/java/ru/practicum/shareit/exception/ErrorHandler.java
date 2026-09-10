package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Глобальный обработчик ошибок формата входных данных на стороне gateway.
 * Бизнес-ошибки (404/403/409/400 бизнес-правил) gateway не порождает сам —
 * они приходят из ответа сервера и прокидываются клиенту как есть, минуя
 * этот обработчик (см. {@link ru.practicum.shareit.client.BaseClient}).
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    /**
     * Обрабатывает ошибки валидации {@code @Valid}/{@code @Validated}-аннотированных DTO.
     *
     * @param e исключение, брошенное Spring MVC при провале Bean Validation
     * @return тело ответа {@code 400 Bad Request} с текстом первой нарушенной валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "Ошибка валидации";
        log.warn("400: {}", message);
        return new ErrorResponse(message);
    }

    /**
     * Обрабатывает отсутствие обязательного заголовка запроса (например, {@code X-Sharer-User-Id}).
     *
     * @param e исключение об отсутствующем заголовке
     * @return тело ответа {@code 400 Bad Request} с именем недостающего заголовка
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingHeader(MissingRequestHeaderException e) {
        log.warn("400: отсутствует заголовок {}", e.getHeaderName());
        return new ErrorResponse("Отсутствует обязательный заголовок " + e.getHeaderName());
    }

    /**
     * Обрабатывает отсутствие обязательного query-параметра (например, {@code text} при поиске).
     *
     * @param e исключение об отсутствующем параметре
     * @return тело ответа {@code 400 Bad Request} с именем недостающего параметра
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingParameter(MissingServletRequestParameterException e) {
        log.warn("400: отсутствует параметр {}", e.getParameterName());
        return new ErrorResponse("Отсутствует обязательный параметр " + e.getParameterName());
    }

    /**
     * Обрабатывает синтаксически некорректное или отсутствующее тело запроса.
     *
     * @param e исключение о нечитаемом теле запроса
     * @return тело ответа {@code 400 Bad Request}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("400: некорректное тело запроса ({})", e.getMessage());
        return new ErrorResponse("Некорректное тело запроса");
    }

    /**
     * Обрабатывает несоответствие типа параметра запроса ожидаемому (например,
     * нечисловой id в пути или в заголовке {@code X-Sharer-User-Id}).
     *
     * @param e исключение о несовпадении типа параметра
     * @return тело ответа {@code 400 Bad Request} с именем параметра
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("400: некорректное значение параметра {}", e.getName());
        return new ErrorResponse("Некорректное значение параметра " + e.getName());
    }
}
