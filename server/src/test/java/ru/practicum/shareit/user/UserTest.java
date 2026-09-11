package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void equals_sameReference_returnsTrue() {
        User user = new User(1L, "Ivan", "ivan@mail.ru");

        assertThat(user.equals(user)).isTrue();
    }

    @Test
    void equals_null_returnsFalse() {
        User user = new User(1L, "Ivan", "ivan@mail.ru");

        assertThat(user.equals(null)).isFalse();
    }

    @Test
    void equals_differentClass_returnsFalse() {
        User user = new User(1L, "Ivan", "ivan@mail.ru");

        assertThat(user.equals("not a user")).isFalse();
    }

    @Test
    void equals_sameId_returnsTrue() {
        User first = new User(1L, "Ivan", "ivan@mail.ru");
        User second = new User(1L, "Другое имя", "other@mail.ru");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void equals_differentId_returnsFalse() {
        User first = new User(1L, "Ivan", "ivan@mail.ru");
        User second = new User(2L, "Ivan", "ivan@mail.ru");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equals_withNullId_returnsFalse() {
        User first = new User();
        User second = new User();

        assertThat(first).isNotEqualTo(second);
    }
}
