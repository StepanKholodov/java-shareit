package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Реализация {@link UserService} поверх {@link UserRepository}.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(saveOrThrowConflict(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = getUserById(userId);

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        return UserMapper.toUserDto(saveOrThrowConflict(user));
    }

    @Override
    public UserDto findById(Long userId) {
        return UserMapper.toUserDto(getUserById(userId));
    }

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        getUserById(userId);
        try {
            userRepository.deleteById(userId);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Невозможно удалить пользователя с id " + userId
                    + ": с ним связаны другие данные (вещи, бронирования, отзывы или запросы)");
        }
    }

    private User saveOrThrowConflict(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (isEmailUniqueViolation(e)) {
                throw new ConflictException("Пользователь с email " + user.getEmail() + " уже существует");
            }
            throw e;
        }
    }

    private boolean isEmailUniqueViolation(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();
        return message != null && message.toUpperCase().contains("UQ_USERS_EMAIL");
    }
}
