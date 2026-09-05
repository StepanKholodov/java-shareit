package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Marker;

/**
 * Представление вещи во внешнем REST API. В отличие от {@link ru.practicum.shareit.item.model.Item}
 * не содержит владельца.
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
}
