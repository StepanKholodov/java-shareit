package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void toUserDto_mapsAllFields() {
        User user = new User(1L, "Ivan", "ivan@mail.ru");

        UserDto dto = UserMapper.toUserDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Ivan");
        assertThat(dto.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void toUser_mapsAllFields() {
        UserDto dto = new UserDto(1L, "Ivan", "ivan@mail.ru");

        User user = UserMapper.toUser(dto);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Ivan");
        assertThat(user.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void constructor_isPrivate() throws Exception {
        Constructor<UserMapper> constructor = UserMapper.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
