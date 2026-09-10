package ru.practicum.shareit.exception;

/**
 * Выбрасывается, когда запрошенный ресурс (пользователь, вещь и т.д.) не найден.
 * Обрабатывается {@link ErrorHandler} и преобразуется в ответ {@code 404 Not Found}.
 */
public class NotFoundException extends RuntimeException {

    /**
     * @param message сообщение об ошибке, которое попадёт в тело ответа
     */
    public NotFoundException(String message) {
        super(message);
    }
}
