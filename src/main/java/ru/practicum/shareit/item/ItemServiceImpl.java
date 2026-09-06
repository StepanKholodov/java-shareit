package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация {@link ItemService} поверх {@link ItemRepository}.
 * Проверку существования пользователя делегирует {@link UserService}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User owner = userService.getUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        userService.getUserById(ownerId);
        Item item = getItemOrThrow(itemId);

        if (!item.isOwnedBy(ownerId)) {
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
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto findById(Long itemId) {
        Item item = getItemOrThrow(itemId);
        ItemDto dto = ItemMapper.toItemDto(item);
        dto.setComments(getComments(itemId));
        return dto;
    }

    @Override
    public Collection<ItemDto> findAllByOwner(Long ownerId) {
        userService.getUserById(ownerId);
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<Comment>> commentsByItemId = commentRepository.findAllByItemIdIn(itemIds).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        Map<Long, List<Booking>> bookingsByItemId = bookingRepository
                .findByItemIdInAndStatusOrderByStartAsc(itemIds, BookingStatus.APPROVED).stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();
        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);
                    dto.setComments(toCommentDtos(commentsByItemId.getOrDefault(item.getId(), List.of())));
                    applyBookings(dto, bookingsByItemId.getOrDefault(item.getId(), List.of()), now);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userService.getUserById(userId);
        Item item = getItemOrThrow(itemId);

        boolean hasCompletedBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!hasCompletedBooking) {
            throw new ValidationException("Пользователь с id " + userId
                    + " не завершал аренду вещи с id " + itemId + ", отзыв недоступен");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private List<CommentDto> getComments(Long itemId) {
        return toCommentDtos(commentRepository.findAllByItemId(itemId));
    }

    private List<CommentDto> toCommentDtos(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private void applyBookings(ItemDto dto, List<Booking> bookings, LocalDateTime now) {
        Booking last = null;
        Booking next = null;
        for (Booking booking : bookings) {
            if (!booking.getStart().isAfter(now)) {
                last = booking;
            } else if (next == null) {
                next = booking;
            }
        }
        if (last != null) {
            dto.setLastBooking(BookingMapper.toItemBookingDto(last));
        }
        if (next != null) {
            dto.setNextBooking(BookingMapper.toItemBookingDto(next));
        }
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }
}
