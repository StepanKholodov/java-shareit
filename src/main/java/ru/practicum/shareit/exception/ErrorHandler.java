package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений веб-слоя. Приводит все ошибки API
 * (как бизнес-исключения приложения, так и стандартные ошибки Spring MVC)
 * к единому формату ответа {@link ErrorResponse}.
 */
@RestControllerAdvice
public class ErrorHandler {

    /**
     * @param e исключение "не найдено"
     * @return тело ответа {@code 404 Not Found}
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * @param e исключение "нет прав на действие"
     * @return тело ответа {@code 403 Forbidden}
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(ForbiddenException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * @param e исключение конфликта с текущим состоянием данных
     * @return тело ответа {@code 409 Conflict}
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(ConflictException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает ошибки валидации {@code @Valid}-аннотированных DTO.
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
        return new ErrorResponse("Некорректное тело запроса");
    }
}
