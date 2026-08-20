package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryItemStorageTest {

    private InMemoryItemStorage storage;
    private User owner;
    private User otherOwner;

    @BeforeEach
    void setUp() {
        storage = new InMemoryItemStorage();
        owner = new User(1L, "Ivan", "ivan@mail.ru");
        otherOwner = new User(2L, "Petr", "petr@mail.ru");
    }

    @Test
    void create_assignsIncrementingIds() {
        Item first = storage.create(new Item(null, "Дрель", "desc", true, owner));
        Item second = storage.create(new Item(null, "Отвертка", "desc", true, owner));

        assertThat(first.getId()).isEqualTo(1L);
        assertThat(second.getId()).isEqualTo(2L);
    }

    @Test
    void update_overwritesExistingItem() {
        Item created = storage.create(new Item(null, "Дрель", "desc", true, owner));
        created.setName("Обновлённая дрель");

        storage.update(created);

        assertThat(storage.findById(created.getId())).contains(created);
    }

    @Test
    void findById_whenMissing_returnsEmptyOptional() {
        assertThat(storage.findById(123L)).isEmpty();
    }

    @Test
    void findAllByOwnerId_returnsOnlyItemsOfThatOwner() {
        storage.create(new Item(null, "Дрель", "desc", true, owner));
        storage.create(new Item(null, "Лобзик", "desc", true, otherOwner));

        assertThat(storage.findAllByOwnerId(1L)).hasSize(1)
                .allSatisfy(item -> assertThat(item.getOwner().getId()).isEqualTo(1L));
    }

    @Test
    void findAllByOwnerId_whenOwnerHasNoItems_returnsEmpty() {
        assertThat(storage.findAllByOwnerId(1L)).isEmpty();
    }

    @Test
    void search_withNullText_returnsEmptyList() {
        storage.create(new Item(null, "Дрель", "desc", true, owner));

        assertThat(storage.search(null)).isEmpty();
    }

    @Test
    void search_withBlankText_returnsEmptyList() {
        storage.create(new Item(null, "Дрель", "desc", true, owner));

        assertThat(storage.search("   ")).isEmpty();
    }

    @Test
    void search_matchesByNameCaseInsensitive() {
        storage.create(new Item(null, "Дрель Салют", "desc", true, owner));

        assertThat(storage.search("сАЛют")).hasSize(1);
    }

    @Test
    void search_matchesByDescriptionCaseInsensitive() {
        storage.create(new Item(null, "Инструмент", "Мощная дрель для бетона", true, owner));

        assertThat(storage.search("дрель")).hasSize(1);
    }

    @Test
    void search_excludesUnavailableItems() {
        storage.create(new Item(null, "Дрель", "desc", false, owner));

        assertThat(storage.search("дрель")).isEmpty();
    }

    @Test
    void search_whenNoMatches_returnsEmptyList() {
        storage.create(new Item(null, "Дрель", "desc", true, owner));

        assertThat(storage.search("шуруповерт")).isEmpty();
    }
}
