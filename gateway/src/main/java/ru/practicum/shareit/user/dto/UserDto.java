package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Marker;

/**
 * Представление пользователя, приходящее от клиента gateway.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым", groups = Marker.OnCreate.class)
    @Size(max = 255, message = "Имя не может быть длиннее 255 символов",
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String name;

    @NotBlank(message = "Email не может быть пустым", groups = Marker.OnCreate.class)
    @Email(message = "Некорректный формат email", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @Size(max = 320, message = "Email не может быть длиннее 320 символов",
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String email;
}
