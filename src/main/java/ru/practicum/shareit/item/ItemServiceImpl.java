package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Реализация {@link ItemService} поверх in-memory хранилища {@link ItemStorage}.
 * Проверку существования пользователя делегирует {@link UserService}.
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User owner = userService.getUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        userService.getUserById(ownerId);
        Item item = getItemOrThrow(itemId);

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Пользователь с id " + ownerId + " не является владельцем вещи с id " + itemId);
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.update(item));
    }

    @Override
    public ItemDto findById(Long itemId) {
        return ItemMapper.toItemDto(getItemOrThrow(itemId));
    }

    @Override
    public Collection<ItemDto> findAllByOwner(Long ownerId) {
        userService.getUserById(ownerId);
        return itemStorage.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        return itemStorage.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item getItemOrThrow(Long itemId) {
        return itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }
}
