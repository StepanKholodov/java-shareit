package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Краткое представление вещи, добавленной в ответ на запрос, встраиваемое
 * в {@link ItemRequestDto}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestItemDto {

    private Long id;
    private String name;
    private Long ownerId;
}
