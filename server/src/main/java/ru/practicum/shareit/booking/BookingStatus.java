package ru.practicum.shareit.booking;

/**
 * Статус бронирования.
 */
public enum BookingStatus {

    /**
     * Ожидает подтверждения владельцем вещи.
     */
    WAITING,

    /**
     * Подтверждено владельцем вещи.
     */
    APPROVED,

    /**
     * Отклонено владельцем вещи.
     */
    REJECTED
}
