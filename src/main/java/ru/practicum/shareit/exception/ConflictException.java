package ru.practicum.shareit.exception;

/**
 * Выбрасывается при конфликте с текущим состоянием данных (например, email уже
 * занят другим пользователем). Обрабатывается {@link ErrorHandler} и преобразуется
 * в ответ {@code 409 Conflict}.
 */
public class ConflictException extends RuntimeException {

    /**
     * @param message сообщение об ошибке, которое попадёт в тело ответа
     */
    public ConflictException(String message) {
        super(message);
    }
}
