package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

/**
 * Хранилище вещей.
 */
public interface ItemStorage {

    /**
     * Сохраняет новую вещь, присваивая ей id.
     *
     * @param item вещь без id
     * @return сохранённая вещь с присвоенным id
     */
    Item create(Item item);

    /**
     * Перезаписывает существующую вещь.
     *
     * @param item вещь с уже присвоенным id
     * @return сохранённая вещь
     */
    Item update(Item item);

    /**
     * @param itemId id вещи
     * @return вещь, если найдена
     */
    Optional<Item> findById(Long itemId);

    /**
     * @param ownerId id владельца
     * @return вещи заданного владельца
     */
    Collection<Item> findAllByOwnerId(Long ownerId);

    /**
     * Ищет доступные для аренды вещи, в названии или описании которых
     * встречается заданный текст (без учёта регистра).
     *
     * @param text текст для поиска; при пустом или {@code null} значении возвращает пустой список
     * @return найденные доступные вещи
     */
    Collection<Item> search(String text);
}
