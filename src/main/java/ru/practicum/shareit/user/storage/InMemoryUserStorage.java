package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация {@link UserStorage}, хранящая пользователей в памяти приложения.
 */
@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public User create(User user) {
        user.setId(idGenerator.incrementAndGet());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean existsByEmail(String email, Long excludeUserId) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email)
                        && !Objects.equals(user.getId(), excludeUserId));
    }
}
