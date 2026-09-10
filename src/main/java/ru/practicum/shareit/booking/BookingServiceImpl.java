package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация {@link BookingService} поверх {@link BookingRepository}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto create(Long bookerId, BookItemRequestDto requestDto) {
        User booker = userService.getUserById(bookerId);
        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + requestDto.getItemId() + " не найдена"));

        if (!requestDto.getStart().isBefore(requestDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования должна быть раньше даты окончания");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id " + item.getId() + " недоступна для бронирования");
        }
        if (item.isOwnedBy(bookerId)) {
            throw new NotFoundException("Владелец не может забронировать собственную вещь с id " + item.getId());
        }

        Booking booking = BookingMapper.toBooking(requestDto, item, booker);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);
        if (!booking.getItem().isOwnedBy(ownerId)) {
            throw new ForbiddenException("Пользователь с id " + ownerId
                    + " не является владельцем вещи из бронирования с id " + bookingId);
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование с id " + bookingId + " уже обработано");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().isOwnedBy(userId);
        if (!isBooker && !isOwner) {
            throw new NotFoundException("Бронирование с id " + bookingId + " не найдено");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> findAllByBooker(Long bookerId, BookingState state) {
        userService.getUserById(bookerId);
        return mapToDto(selectByState(state, bookerId, false));
    }

    @Override
    public Collection<BookingDto> findAllByOwner(Long ownerId, BookingState state) {
        userService.getUserById(ownerId);
        return mapToDto(selectByState(state, ownerId, true));
    }

    private List<Booking> selectByState(BookingState state, Long id, boolean isOwner) {
        LocalDateTime now = LocalDateTime.now();
        return switch (state) {
            case ALL -> isOwner
                    ? bookingRepository.findByItemOwnerIdOrderByStartDesc(id)
                    : bookingRepository.findByBookerIdOrderByStartDesc(id);
            case CURRENT -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(id, now, now)
                    : bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(id, now, now);
            case PAST -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(id, now)
                    : bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(id, now);
            case FUTURE -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(id, now)
                    : bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(id, now);
            case WAITING -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(id, BookingStatus.WAITING)
                    : bookingRepository.findByBookerIdAndStatusOrderByStartDesc(id, BookingStatus.WAITING);
            case REJECTED -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(id, BookingStatus.REJECTED)
                    : bookingRepository.findByBookerIdAndStatusOrderByStartDesc(id, BookingStatus.REJECTED);
        };
    }

    private List<BookingDto> mapToDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
    }
}
