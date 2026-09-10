package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestTest {

    private ItemRequest requestWithId(Long id) {
        ItemRequest request = new ItemRequest();
        request.setId(id);
        return request;
    }

    @Test
    void equals_sameReference_returnsTrue() {
        ItemRequest request = requestWithId(1L);

        assertThat(request.equals(request)).isTrue();
    }

    @Test
    void equals_null_returnsFalse() {
        ItemRequest request = requestWithId(1L);

        assertThat(request.equals(null)).isFalse();
    }

    @Test
    void equals_differentClass_returnsFalse() {
        ItemRequest request = requestWithId(1L);

        assertThat(request.equals("not a request")).isFalse();
    }

    @Test
    void equals_sameId_returnsTrue() {
        ItemRequest first = requestWithId(1L);
        ItemRequest second = requestWithId(1L);
        second.setDescription("Другое описание");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void equals_differentId_returnsFalse() {
        ItemRequest first = requestWithId(1L);
        ItemRequest second = requestWithId(2L);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equals_withNullId_returnsFalse() {
        ItemRequest first = new ItemRequest();
        ItemRequest second = new ItemRequest();

        assertThat(first).isNotEqualTo(second);
    }
}
