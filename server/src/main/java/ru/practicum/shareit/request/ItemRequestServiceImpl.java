package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация {@link ItemRequestService} поверх {@link ItemRequestRepository}.
 * Проверку существования пользователя делегирует {@link UserService}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestDto create(Long requestorId, ItemRequestDto requestDto) {
        User requestor = userService.getUserById(requestorId);
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto, requestor);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(request), List.of());
    }

    @Override
    public Collection<ItemRequestDto> findOwn(Long requestorId) {
        userService.getUserById(requestorId);
        return toDtosWithItems(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId));
    }

    @Override
    public Collection<ItemRequestDto> findAllByOthers(Long userId) {
        userService.getUserById(userId);
        return toDtosWithItems(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId));
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        userService.getUserById(userId);
        ItemRequest request = getRequestOrThrow(requestId);
        List<Item> items = itemRepository.findAllByRequestIdIn(List.of(requestId));
        return ItemRequestMapper.toItemRequestDto(request, items);
    }

    private List<ItemRequestDto> toDtosWithItems(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        Map<Long, List<Item>> itemsByRequestId = itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(
                        request, itemsByRequestId.getOrDefault(request.getId(), List.of())))
                .collect(Collectors.toList());
    }

    private ItemRequest getRequestOrThrow(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));
    }
}
