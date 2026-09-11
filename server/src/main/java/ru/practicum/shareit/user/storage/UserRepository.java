package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

/**
 * JPA-репозиторий пользователей.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
