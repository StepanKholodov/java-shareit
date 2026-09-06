package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class EntityUtilsTest {

    @Test
    void getEffectiveClass_forPlainEntity_returnsItsClass() {
        User user = new User();

        assertThat(EntityUtils.getEffectiveClass(user)).isEqualTo(User.class);
    }

    @Test
    void constructor_isPrivate() throws Exception {
        Constructor<EntityUtils> constructor = EntityUtils.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
