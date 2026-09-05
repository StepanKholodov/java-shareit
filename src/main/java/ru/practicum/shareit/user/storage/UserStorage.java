package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

/**
 * Хранилище пользователей.
 */
public interface UserStorage {

    /**
     * Сохраняет нового пользователя, присваивая ему id.
     *
     * @param user пользователь без id
     * @return сохранённый пользователь с присвоенным id
     */
    User create(User user);

    /**
     * Перезаписывает существующего пользователя.
     *
     * @param user пользователь с уже присвоенным id
     * @return сохранённый пользователь
     */
    User update(User user);

    /**
     * @param userId id пользователя
     * @return пользователь, если найден
     */
    Optional<User> findById(Long userId);

    /**
     * @return все сохранённые пользователи
     */
    Collection<User> findAll();

    /**
     * Удаляет пользователя. Если пользователя с таким id нет — ничего не делает.
     *
     * @param userId id удаляемого пользователя
     */
    void delete(Long userId);

    /**
     * Проверяет, занят ли email другим пользователем.
     *
     * @param email          проверяемый email (сравнение без учёта регистра)
     * @param excludeUserId  id пользователя, которого нужно исключить из проверки
     *                       (используется при обновлении собственного email), либо {@code null}
     * @return {@code true}, если email уже используется другим пользователем
     */
    boolean existsByEmail(String email, Long excludeUserId);
}
