package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Marker;

/**
 * Представление вещи, приходящее от клиента gateway.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название не может быть пустым", groups = Marker.OnCreate.class)
    @Size(max = 255, message = "Название не может быть длиннее 255 символов",
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = Marker.OnCreate.class)
    @Size(max = 2000, message = "Описание не может быть длиннее 2000 символов",
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String description;

    @NotNull(message = "Не указан статус доступности", groups = Marker.OnCreate.class)
    private Boolean available;

    private Long requestId;

    public ItemDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
