package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

/**
 * Бизнес-логика работы с запросами на вещи.
 */
public interface ItemRequestService {

    /**
     * Создаёт новый запрос на вещь.
     *
     * @param requestorId id пользователя, создающего запрос
     * @param requestDto  данные запроса (описание)
     * @return созданный запрос с присвоенным id и временем создания
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     */
    ItemRequestDto create(Long requestorId, ItemRequestDto requestDto);

    /**
     * Возвращает собственные запросы пользователя вместе с вещами, добавленными в ответ на них,
     * от новых к старым.
     *
     * @param requestorId id пользователя
     * @return запросы пользователя
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     */
    Collection<ItemRequestDto> findOwn(Long requestorId);

    /**
     * Возвращает запросы других пользователей вместе с вещами, добавленными в ответ на них,
     * от новых к старым.
     *
     * @param userId id пользователя, выполняющего запрос
     * @return запросы других пользователей
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь не найден
     */
    Collection<ItemRequestDto> findAllByOthers(Long userId);

    /**
     * Возвращает запрос по id вместе с вещами, добавленными в ответ на него.
     *
     * @param userId    id пользователя, выполняющего запрос
     * @param requestId id запроса
     * @return найденный запрос
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь или запрос не найдены
     */
    ItemRequestDto findById(Long userId, Long requestId);
}
