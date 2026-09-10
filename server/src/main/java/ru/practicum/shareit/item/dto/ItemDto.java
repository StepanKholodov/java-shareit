package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.ItemBookingDto;

import java.util.List;

/**
 * Представление вещи во внешнем REST API. В отличие от {@link ru.practicum.shareit.item.model.Item}
 * не содержит владельца. Поля {@code comments}, {@code lastBooking} и {@code nextBooking}
 * заполняются только при просмотре вещи (остаются {@code null}/пустыми в ответах на
 * создание/редактирование, но само поле всегда присутствует в JSON). Валидация формата
 * входных данных выполняется на стороне gateway.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;

    public ItemDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
