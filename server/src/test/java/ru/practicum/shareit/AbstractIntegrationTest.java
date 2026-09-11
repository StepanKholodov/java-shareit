package ru.practicum.shareit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

/**
 * Общая база для интеграционных тестов на реальной (H2) базе: без транзакции
 * на самом тесте, границу сессии задаёт только {@code @Transactional}
 * сервисных методов.
 */
@SpringBootTest
public abstract class AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    protected User createUser(String email) {
        return userRepository.save(new User(null, "User " + email, email));
    }
}
