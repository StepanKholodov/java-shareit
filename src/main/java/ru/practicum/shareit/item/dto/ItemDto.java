package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.validation.Marker;

import java.util.List;

/**
 * Представление вещи во внешнем REST API. В отличие от {@link ru.practicum.shareit.item.model.Item}
 * не содержит владельца. Поля {@code comments}, {@code lastBooking} и {@code nextBooking}
 * заполняются только при просмотре вещи (остаются {@code null}/пустыми в ответах на
 * создание/редактирование, но само поле всегда присутствует в JSON).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название не может быть пустым", groups = Marker.OnCreate.class)
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = Marker.OnCreate.class)
    private String description;

    @NotNull(message = "Не указан статус доступности", groups = Marker.OnCreate.class)
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
