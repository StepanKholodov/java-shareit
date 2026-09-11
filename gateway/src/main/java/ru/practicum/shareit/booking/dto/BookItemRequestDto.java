package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Запрос на создание бронирования, приходящий от клиента gateway.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookItemRequestDto {

    @NotNull(message = "Не указана вещь для бронирования")
    private Long itemId;

    @NotNull(message = "Не указана дата начала бронирования")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Не указана дата окончания бронирования")
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;

    /**
     * Кросс-полевая проверка, не требующая обращения к БД, поэтому выполняется
     * здесь, а не на сервере. При {@code null} в {@code start}/{@code end}
     * ничего не проверяет — за это уже отвечают {@code @NotNull} на самих полях.
     *
     * @return {@code true}, если дата окончания позже даты начала
     */
    @AssertTrue(message = "Дата окончания бронирования должна быть позже даты начала")
    private boolean isEndAfterStart() {
        return start == null || end == null || end.isAfter(start);
    }
}
