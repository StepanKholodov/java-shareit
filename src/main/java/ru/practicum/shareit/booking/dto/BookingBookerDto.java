package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Краткое представление автора бронирования внутри {@link BookingDto}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingBookerDto {

    private Long id;
}
