package ru.practicum.shareit.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.Hibernate.isInitialized;

/**
 * Проверяет {@link EntityUtils#getEffectiveClass(Object)} на настоящем Hibernate-прокси:
 * прокси — это отдельный сгенерированный подкласс, и без разворачивания до
 * персистентного класса сравнение с обычной сущностью ошибочно считалось бы неравным.
 */
@SpringBootTest
class EntityUtilsIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void getEffectiveClass_forHibernateProxy_returnsPersistentClass() {
        User saved = userRepository.save(new User(null, "Ivan", "proxy-test@mail.ru"));
        entityManager.clear();

        User proxy = entityManager.getReference(User.class, saved.getId());

        assertThat(isInitialized(proxy)).isFalse();
        assertThat(EntityUtils.getEffectiveClass(proxy)).isEqualTo(User.class);
    }
}
