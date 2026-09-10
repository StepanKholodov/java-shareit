package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверяет формат сериализации даты создания отзыва.
 */
@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void serialize_writesCreatedAsIsoString() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 6, 1, 10, 15, 30);
        CommentDto dto = new CommentDto(1L, "Отличная дрель", "Ivan", created);

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная дрель");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-06-01T10:15:30");
    }

    @Test
    void deserialize_parsesIsoCreatedBack() throws Exception {
        String content = "{\"id\":1,\"text\":\"Отличная дрель\",\"authorName\":\"Ivan\",\"created\":\"2024-06-01T10:15:30\"}";

        CommentDto result = json.parseObject(content);

        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2024, 6, 1, 10, 15, 30));
    }
}
