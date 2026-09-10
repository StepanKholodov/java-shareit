package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверяет формат сериализации {@code LocalDateTime} (ISO-строка, не timestamp)
 * и корректность обратного разбора, от которых зависит формат ответа REST API.
 */
@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serialize_writesDatesAsIsoStringsAndStatusAsName() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 10, 15, 30);
        LocalDateTime end = LocalDateTime.of(2024, 6, 2, 11, 30, 45);
        BookingDto dto = new BookingDto(1L, start, end, BookingStatus.APPROVED,
                new BookingItemDto(10L, "Дрель"), new BookingBookerDto(2L));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-06-01T10:15:30");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-06-02T11:30:45");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
    }

    @Test
    void deserialize_parsesIsoDatesAndStatusBack() throws Exception {
        String content = "{\"id\":1,\"start\":\"2024-06-01T10:15:30\",\"end\":\"2024-06-02T11:30:45\","
                + "\"status\":\"APPROVED\",\"item\":{\"id\":10,\"name\":\"Дрель\"},\"booker\":{\"id\":2}}";

        BookingDto result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 6, 1, 10, 15, 30));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 6, 2, 11, 30, 45));
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(result.getItem().getName()).isEqualTo("Дрель");
        assertThat(result.getBooker().getId()).isEqualTo(2L);
    }
}
