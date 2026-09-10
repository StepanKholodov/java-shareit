package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {

    @Test
    void constructor_leavesRequestNull() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");

        Item item = new Item(1L, "Дрель", "Простая дрель", true, owner);

        assertThat(item.getRequest()).isNull();
    }

    @Test
    void setRequest_storesValue() {
        ItemRequest request = new ItemRequest();
        Item item = new Item();

        item.setRequest(request);

        assertThat(item.getRequest()).isEqualTo(request);
    }

    @Test
    void isOwnedBy_whenSameId_returnsTrue() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        Item item = new Item(1L, "Дрель", "Простая дрель", true, owner);

        assertThat(item.isOwnedBy(1L)).isTrue();
    }

    @Test
    void isOwnedBy_whenDifferentId_returnsFalse() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        Item item = new Item(1L, "Дрель", "Простая дрель", true, owner);

        assertThat(item.isOwnedBy(2L)).isFalse();
    }

    @Test
    void equals_sameReference_returnsTrue() {
        Item item = new Item(1L, "Дрель", "desc", true, null);

        assertThat(item.equals(item)).isTrue();
    }

    @Test
    void equals_null_returnsFalse() {
        Item item = new Item(1L, "Дрель", "desc", true, null);

        assertThat(item.equals(null)).isFalse();
    }

    @Test
    void equals_differentClass_returnsFalse() {
        Item item = new Item(1L, "Дрель", "desc", true, null);

        assertThat(item.equals("not an item")).isFalse();
    }

    @Test
    void equals_sameId_returnsTrue() {
        Item first = new Item(1L, "Дрель", "desc", true, null);
        Item second = new Item(1L, "Другое название", "other", false, null);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void equals_differentId_returnsFalse() {
        Item first = new Item(1L, "Дрель", "desc", true, null);
        Item second = new Item(2L, "Дрель", "desc", true, null);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equals_withNullId_returnsFalse() {
        Item first = new Item();
        Item second = new Item();

        assertThat(first).isNotEqualTo(second);
    }
}
