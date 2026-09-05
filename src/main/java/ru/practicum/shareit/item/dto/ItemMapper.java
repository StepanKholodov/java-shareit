package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

/**
 * Преобразование между моделью {@link Item} и {@link ItemDto}.
 */
public final class ItemMapper {

    private ItemMapper() {
    }

    /**
     * @param item сущность вещи
     * @return DTO для отдачи через REST API (без владельца)
     */
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    /**
     * @param itemDto DTO, полученный из запроса
     * @param owner   владелец, которому будет принадлежать вещь
     * @return сущность вещи
     */
    public static Item toItem(ItemDto itemDto, User owner) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), owner);
    }
}
