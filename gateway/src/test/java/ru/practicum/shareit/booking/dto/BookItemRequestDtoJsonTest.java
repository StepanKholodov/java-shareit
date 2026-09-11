package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверяет, что даты из тела запроса на бронирование корректно разбираются
 * в {@code LocalDateTime} — от этого зависит, сможет ли Bean Validation
 * ({@code @FutureOrPresent}/{@code @Future}) вообще увидеть правильные значения.
 */
@JsonTest
class BookItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    void deserialize_parsesIsoDatesIntoLocalDateTime() throws Exception {
        String content = "{\"itemId\":10,\"start\":\"2024-06-01T10:15:30\",\"end\":\"2024-06-02T11:30:45\"}";

        BookItemRequestDto result = json.parseObject(content);

        assertThat(result.getItemId()).isEqualTo(10L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 6, 1, 10, 15, 30));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 6, 2, 11, 30, 45));
    }

    @Test
    void serialize_writesDatesAsIsoStrings() throws Exception {
        BookItemRequestDto dto = new BookItemRequestDto(10L,
                LocalDateTime.of(2024, 6, 1, 10, 15, 30), LocalDateTime.of(2024, 6, 2, 11, 30, 45));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-06-01T10:15:30");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-06-02T11:30:45");
    }
}
