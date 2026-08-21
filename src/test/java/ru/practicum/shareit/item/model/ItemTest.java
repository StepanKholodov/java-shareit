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
}
