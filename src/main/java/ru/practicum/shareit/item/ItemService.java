package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

/**
 * Бизнес-логика работы с вещами.
 */
public interface ItemService {

    /**
     * Добавляет новую вещь. Владельцем становится пользователь с id {@code ownerId}.
     *
     * @param ownerId id владельца вещи
     * @param itemDto данные вещи (название, описание, доступность)
     * @return созданная вещь с присвоенным id
     * @throws ru.practicum.shareit.exception.NotFoundException если владелец не найден
     */
    ItemDto create(Long ownerId, ItemDto itemDto);

    /**
     * Частично обновляет вещь: заполненные поля {@code itemDto} заменяют текущие
     * значения, пустые/{@code null} — игнорируются. Редактировать вещь может
     * только её владелец.
     *
     * @param ownerId id пользователя, выполняющего запрос
     * @param itemId  id обновляемой вещи
     * @param itemDto новые значения полей
     * @return обновлённая вещь
     * @throws ru.practicum.shareit.exception.NotFoundException  если владелец или вещь не найдены
     * @throws ru.practicum.shareit.exception.ForbiddenException если запрос выполняет не владелец вещи
     */
    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto);

    /**
     * Возвращает вещь по id. Доступно любому пользователю.
     *
     * @param itemId id вещи
     * @return найденная вещь
     * @throws ru.practicum.shareit.exception.NotFoundException если вещь не найдена
     */
    ItemDto findById(Long itemId);

    /**
     * Возвращает все вещи заданного владельца.
     *
     * @param ownerId id владельца
     * @return список вещей владельца
     * @throws ru.practicum.shareit.exception.NotFoundException если владелец не найден
     */
    Collection<ItemDto> findAllByOwner(Long ownerId);

    /**
     * Ищет доступные для аренды вещи, в названии или описании которых
     * встречается заданный текст (без учёта регистра).
     *
     * @param text текст для поиска; при пустом или {@code null} значении возвращает пустой список
     * @return найденные доступные вещи
     */
    Collection<ItemDto> search(String text);
}
