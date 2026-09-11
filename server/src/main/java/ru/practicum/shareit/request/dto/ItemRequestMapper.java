package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Преобразование между моделью {@link ItemRequest} и его DTO-представлениями.
 */
public final class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    /**
     * @param requestDto запрос на создание, полученный от клиента
     * @param requestor  пользователь, создающий запрос
     * @return новая сущность запроса с текущим временем создания
     */
    public static ItemRequest toItemRequest(ItemRequestDto requestDto, User requestor) {
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    /**
     * @param request сущность запроса
     * @param items   вещи, добавленные в ответ на этот запрос
     * @return DTO для отдачи через REST API
     */
    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items) {
        List<ItemRequestItemDto> itemDtos = items.stream()
                .map(item -> new ItemRequestItemDto(item.getId(), item.getName(), item.getOwner().getId()))
                .collect(Collectors.toList());
        return new ItemRequestDto(request.getId(), request.getDescription(), request.getCreated(), itemDtos);
    }
}
