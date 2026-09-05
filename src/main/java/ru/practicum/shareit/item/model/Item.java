package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * Вещь, которой пользователь (владелец) готов поделиться.
 */
@Data
@NoArgsConstructor
public class Item {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;

    /**
     * Запрос, в ответ на который была добавлена эта вещь. {@code null}, если вещь
     * добавлена не по запросу. Полноценно используется начиная со спринта add-item-requests.
     */
    private ItemRequest request;

    public Item(Long id, String name, String description, Boolean available, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
