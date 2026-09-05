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
     * @param userDto DTO, полученный из запроса
     * @return сущность пользователя
     */
    public static User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
