package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void toItemDto_mapsAllFieldsExceptOwner() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        Item item = new Item(1L, "Дрель", "Простая дрель", true, owner);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("Простая дрель");
        assertThat(dto.getAvailable()).isTrue();
    }

    @Test
    void toItem_mapsAllFieldsAndAssignsOwner() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        ItemDto dto = new ItemDto(null, "Дрель", "Простая дрель", true);

        Item item = ItemMapper.toItem(dto, owner, null);

        assertThat(item.getName()).isEqualTo("Дрель");
        assertThat(item.getDescription()).isEqualTo("Простая дрель");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isNull();
    }

    @Test
    void toItem_ignoresClientSuppliedId() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        ItemDto dto = new ItemDto(99L, "Дрель", "Простая дрель", true);

        Item item = ItemMapper.toItem(dto, owner, null);

        assertThat(item.getId()).isNull();
    }

    @Test
    void toItem_withRequest_assignsRequest() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        ItemRequest request = new ItemRequest();
        request.setId(5L);
        ItemDto dto = new ItemDto(1L, "Дрель", "Простая дрель", true);

        Item item = ItemMapper.toItem(dto, owner, request);

        assertThat(item.getRequest()).isEqualTo(request);
    }

    @Test
    void toItemDto_withRequest_mapsRequestId() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        ItemRequest request = new ItemRequest();
        request.setId(5L);
        Item item = new Item(1L, "Дрель", "Простая дрель", true, owner);
        item.setRequest(request);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getRequestId()).isEqualTo(5L);
    }

    @Test
    void constructor_isPrivate() throws Exception {
        Constructor<ItemMapper> constructor = ItemMapper.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
