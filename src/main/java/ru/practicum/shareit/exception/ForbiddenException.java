package ru.practicum.shareit.exception;

/**
 * Выбрасывается при попытке выполнить действие, на которое у пользователя нет прав
 * (например, редактирование чужой вещи). Обрабатывается {@link ErrorHandler}
 * и преобразуется в ответ {@code 403 Forbidden}.
 */
public class ForbiddenException extends RuntimeException {

    /**
     * @param message сообщение об ошибке, которое попадёт в тело ответа
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
