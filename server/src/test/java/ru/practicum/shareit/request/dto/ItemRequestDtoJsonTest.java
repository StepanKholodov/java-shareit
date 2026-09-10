package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверяет формат сериализации даты создания запроса и вложенного списка вещей.
 */
@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void serialize_writesCreatedAsIsoStringAndNestedItems() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 6, 1, 10, 15, 30);
        ItemRequestDto dto = new ItemRequestDto(5L, "Нужна дрель", created,
                List.of(new ItemRequestItemDto(10L, "Дрель", 2L)));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-06-01T10:15:30");
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(2);
    }

    @Test
    void deserialize_parsesIsoCreatedBack() throws Exception {
        String content = "{\"id\":5,\"description\":\"Нужна дрель\",\"created\":\"2024-06-01T10:15:30\",\"items\":[]}";

        ItemRequestDto result = json.parseObject(content);

        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2024, 6, 1, 10, 15, 30));
        assertThat(result.getItems()).isEmpty();
    }
}
