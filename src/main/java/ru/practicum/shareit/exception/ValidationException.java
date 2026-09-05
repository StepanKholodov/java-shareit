package ru.practicum.shareit.exception;

/**
 * Выбрасывается при нарушении бизнес-правил, которые не покрываются
 * аннотациями Bean Validation (например, бронирование недоступной вещи,
 * попытка оставить отзыв без завершённой аренды). Обрабатывается
 * {@link ErrorHandler} и преобразуется в ответ {@code 400 Bad Request}.
 */
public class ValidationException extends RuntimeException {

    /**
     * @param message сообщение об ошибке, которое попадёт в тело ответа
     */
    public ValidationException(String message) {
        super(message);
    }
}
