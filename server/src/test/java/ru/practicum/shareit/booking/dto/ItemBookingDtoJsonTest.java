package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверяет формат сериализации дат в кратком представлении бронирования,
 * встраиваемом в {@code ItemDto.lastBooking}/{@code nextBooking}.
 */
@JsonTest
class ItemBookingDtoJsonTest {

    @Autowired
    private JacksonTester<ItemBookingDto> json;

    @Test
    void serialize_writesDatesAsIsoStrings() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 10, 15, 30);
        LocalDateTime end = LocalDateTime.of(2024, 6, 2, 11, 30, 45);
        ItemBookingDto dto = new ItemBookingDto(1L, 2L, start, end);

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-06-01T10:15:30");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-06-02T11:30:45");
    }

    @Test
    void deserialize_parsesIsoDatesBack() throws Exception {
        String content = "{\"id\":1,\"bookerId\":2,\"start\":\"2024-06-01T10:15:30\",\"end\":\"2024-06-02T11:30:45\"}";

        ItemBookingDto result = json.parseObject(content);

        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 6, 1, 10, 15, 30));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 6, 2, 11, 30, 45));
    }
}
