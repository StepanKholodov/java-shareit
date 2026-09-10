package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Краткое представление бронирования, встраиваемое в {@code ItemDto}
 * как последнее ({@code lastBooking}) или ближайшее следующее ({@code nextBooking})
 * бронирование вещи — видно только владельцу в списке его вещей.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemBookingDto {

    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
