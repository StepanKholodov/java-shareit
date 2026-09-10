package ru.practicum.shareit.booking;

import java.util.Optional;

/**
 * Фильтр состояния бронирований для параметра {@code state} в GET-запросах списка бронирований.
 */
public enum BookingState {

    /**
     * Все бронирования.
     */
    ALL,

    /**
     * Текущие — дата начала уже наступила, дата окончания ещё не наступила.
     */
    CURRENT,

    /**
     * Завершённые — дата окончания уже наступила.
     */
    PAST,

    /**
     * Будущие — дата начала ещё не наступила.
     */
    FUTURE,

    /**
     * Ожидающие подтверждения владельцем.
     */
    WAITING,

    /**
     * Отклонённые владельцем.
     */
    REJECTED;

    /**
     * Разбирает строковое значение параметра {@code state} без выбрасывания исключения.
     *
     * @param stringState значение параметра, как оно пришло в запросе
     * @return распознанное значение, либо {@link Optional#empty()}, если оно не поддерживается
     */
    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equals(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
