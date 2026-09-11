package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Представление отзыва на вещь, приходящее от клиента gateway.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank(message = "Текст отзыва не может быть пустым")
    @Size(max = 2000, message = "Текст отзыва не может быть длиннее 2000 символов")
    private String text;
}
