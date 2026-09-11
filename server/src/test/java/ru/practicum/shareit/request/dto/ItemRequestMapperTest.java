package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    @Test
    void toItemRequest_mapsDescriptionAndRequestorAndSetsCreated() {
        User requestor = new User(1L, "Ivan", "ivan@mail.ru");
        ItemRequestDto requestDto = new ItemRequestDto(null, "Нужна дрель", null, null);

        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto, requestor);

        assertThat(request.getDescription()).isEqualTo("Нужна дрель");
        assertThat(request.getRequestor()).isEqualTo(requestor);
        assertThat(request.getCreated()).isNotNull();
    }

    @Test
    void toItemRequestDto_withoutItems_mapsFieldsAndEmptyItems() {
        User requestor = new User(1L, "Ivan", "ivan@mail.ru");
        ItemRequest request = new ItemRequest();
        request.setId(5L);
        request.setDescription("Нужна дрель");
        request.setRequestor(requestor);
        LocalDateTime created = LocalDateTime.now();
        request.setCreated(created);

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request, List.of());

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getCreated()).isEqualTo(created);
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void toItemRequestDto_withItems_mapsItemsShortDto() {
        User requestor = new User(1L, "Ivan", "ivan@mail.ru");
        User owner = new User(2L, "Petr", "petr@mail.ru");
        ItemRequest request = new ItemRequest();
        request.setId(5L);
        request.setDescription("Нужна дрель");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        Item item = new Item(10L, "Дрель", "desc", true, owner);

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request, List.of(item));

        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(10L);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Дрель");
        assertThat(dto.getItems().get(0).getOwnerId()).isEqualTo(2L);
    }

    @Test
    void constructor_isPrivate() throws Exception {
        Constructor<ItemRequestMapper> constructor = ItemRequestMapper.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
