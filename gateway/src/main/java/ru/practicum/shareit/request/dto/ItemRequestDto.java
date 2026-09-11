package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос на создание запроса на вещь, приходящий от клиента gateway.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 2000, message = "Описание не может быть длиннее 2000 символов")
    private String description;
}
