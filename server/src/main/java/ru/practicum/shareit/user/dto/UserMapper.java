package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

/**
 * Преобразование между моделью {@link User} и {@link UserDto}.
 */
public final class UserMapper {

    private UserMapper() {
    }

    /**
     * @param user сущность пользователя
     * @return DTO для отдачи через REST API
     */
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    /**
     * Используется только при создании пользователя, поэтому {@code id} из DTO
     * намеренно игнорируется: доверять клиентскому {@code id} нельзя — иначе
     * {@code JpaRepository.save()} посчитает переданную с непустым id сущность
     * уже существующей и молча перезапишет чужую запись вместо создания новой.
     *
     * @param userDto DTO, полученный из запроса на создание
     * @return новая сущность пользователя без id
     */
    public static User toUser(UserDto userDto) {
        return new User(null, userDto.getName(), userDto.getEmail());
    }
}
