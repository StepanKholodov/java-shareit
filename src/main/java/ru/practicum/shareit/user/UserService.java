package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * Бизнес-логика работы с пользователями.
 */
public interface UserService {

    /**
     * Возвращает сущность пользователя. Предназначен для внутреннего использования
     * другими сервисами (например, {@code ItemService} при проверке владельца вещи).
     *
     * @param userId id пользователя
     * @return найденный пользователь
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     */
    User getUserById(Long userId);

    /**
     * Создаёт нового пользователя.
     *
     * @param userDto данные создаваемого пользователя
     * @return созданный пользователь с присвоенным id
     * @throws ru.practicum.shareit.exception.ConflictException если email уже занят
     */
    UserDto create(UserDto userDto);

    /**
     * Частично обновляет пользователя: заполненные поля {@code userDto}
     * заменяют текущие значения, пустые/{@code null} — игнорируются.
     *
     * @param userId  id обновляемого пользователя
     * @param userDto новые значения полей
     * @return обновлённый пользователь
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     * @throws ru.practicum.shareit.exception.ConflictException если новый email уже занят другим пользователем
     */
    UserDto update(Long userId, UserDto userDto);

    /**
     * Возвращает пользователя по id.
     *
     * @param userId id пользователя
     * @return найденный пользователь
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     */
    UserDto findById(Long userId);

    /**
     * Возвращает всех зарегистрированных пользователей.
     *
     * @return список пользователей
     */
    Collection<UserDto> findAll();

    /**
     * Удаляет пользователя.
     *
     * @param userId id удаляемого пользователя
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     */
    void delete(Long userId);
}
