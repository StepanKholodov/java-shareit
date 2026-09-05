package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryUserStorageTest {

    private InMemoryUserStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryUserStorage();
    }

    @Test
    void create_assignsIncrementingIds() {
        User first = storage.create(new User(null, "Ivan", "ivan@mail.ru"));
        User second = storage.create(new User(null, "Petr", "petr@mail.ru"));

        assertThat(first.getId()).isEqualTo(1L);
        assertThat(second.getId()).isEqualTo(2L);
    }

    @Test
    void update_overwritesExistingUser() {
        User created = storage.create(new User(null, "Ivan", "ivan@mail.ru"));
        created.setName("Updated");

        storage.update(created);

        assertThat(storage.findById(created.getId())).contains(created);
    }

    @Test
    void findById_whenMissing_returnsEmptyOptional() {
        assertThat(storage.findById(123L)).isEmpty();
    }

    @Test
    void findAll_returnsAllCreatedUsers() {
        storage.create(new User(null, "Ivan", "ivan@mail.ru"));
        storage.create(new User(null, "Petr", "petr@mail.ru"));

        assertThat(storage.findAll()).hasSize(2);
    }

    @Test
    void delete_removesUser() {
        User created = storage.create(new User(null, "Ivan", "ivan@mail.ru"));

        storage.delete(created.getId());

        assertThat(storage.findById(created.getId())).isEmpty();
    }

    @Test
    void existsByEmail_whenSameEmailDifferentUser_returnsTrue() {
        storage.create(new User(null, "Ivan", "Ivan@Mail.ru"));

        assertThat(storage.existsByEmail("ivan@mail.ru", null)).isTrue();
    }

    @Test
    void existsByEmail_whenExcludingOwner_returnsFalse() {
        User created = storage.create(new User(null, "Ivan", "ivan@mail.ru"));

        assertThat(storage.existsByEmail("ivan@mail.ru", created.getId())).isFalse();
    }

    @Test
    void existsByEmail_whenNoMatch_returnsFalse() {
        storage.create(new User(null, "Ivan", "ivan@mail.ru"));

        assertThat(storage.existsByEmail("unknown@mail.ru", null)).isFalse();
    }
}
