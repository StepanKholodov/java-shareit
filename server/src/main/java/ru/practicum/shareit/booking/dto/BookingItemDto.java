package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Краткое представление вещи внутри {@link BookingDto}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingItemDto {

    private Long id;
    private String name;
}
