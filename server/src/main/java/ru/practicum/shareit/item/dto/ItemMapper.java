package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
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
        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    /**
     * @param itemDto DTO, полученный из запроса
     * @param owner   владелец, которому будет принадлежать вещь
     * @param request запрос, в ответ на который добавляется вещь, либо {@code null}
     * @return сущность вещи
     */
    public static Item toItem(ItemDto itemDto, User owner, ItemRequest request) {
        Item item = new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), owner);
        item.setRequest(request);
        return item;
    }
}
