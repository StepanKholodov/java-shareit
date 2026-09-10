package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Запрос на создание бронирования, приходящий от клиента. Валидация формата
 * входных данных выполняется на стороне gateway.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookItemRequestDto {

    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
