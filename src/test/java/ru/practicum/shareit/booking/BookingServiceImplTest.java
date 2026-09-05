package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item availableItem;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Ivan", "ivan@mail.ru");
        booker = new User(2L, "Petr", "petr@mail.ru");
        availableItem = new Item(10L, "Дрель", "desc", true, owner);
    }

    private BookItemRequestDto validRequest() {
        return new BookItemRequestDto(10L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    }

    private Booking waitingBooking() {
        Booking booking = new Booking();
        booking.setId(100L);
        booking.setItem(availableItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    @Test
    void create_whenValid_savesBookingInWaitingStatus() {
        when(userService.getUserById(2L)).thenReturn(booker);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(availableItem));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(100L);
            return booking;
        });

        BookingDto result = bookingService.create(2L, validRequest());

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getItem().getId()).isEqualTo(10L);
        assertThat(result.getBooker().getId()).isEqualTo(2L);
    }

    @Test
    void create_whenItemNotFound_throwsNotFoundException() {
        when(userService.getUserById(2L)).thenReturn(booker);
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(2L, validRequest())).isInstanceOf(NotFoundException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenItemUnavailable_throwsValidationException() {
        availableItem.setAvailable(false);
        when(userService.getUserById(2L)).thenReturn(booker);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(availableItem));

        assertThatThrownBy(() -> bookingService.create(2L, validRequest())).isInstanceOf(ValidationException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenStartAfterEnd_throwsValidationException() {
        when(userService.getUserById(2L)).thenReturn(booker);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(availableItem));
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                10L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> bookingService.create(2L, invalidRequest)).isInstanceOf(ValidationException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenBookerIsOwner_throwsNotFoundException() {
        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(availableItem));

        assertThatThrownBy(() -> bookingService.create(1L, validRequest())).isInstanceOf(NotFoundException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_whenOwnerApproves_setsApprovedStatus() {
        Booking booking = waitingBooking();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.approve(1L, 100L, true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approve_whenOwnerRejects_setsRejectedStatus() {
        Booking booking = waitingBooking();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.approve(1L, 100L, false);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approve_whenNotOwner_throwsForbiddenException() {
        Booking booking = waitingBooking();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approve(2L, 100L, true)).isInstanceOf(ForbiddenException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_whenAlreadyProcessed_throwsValidationException() {
        Booking booking = waitingBooking();
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approve(1L, 100L, true)).isInstanceOf(ValidationException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_whenBookingNotFound_throwsNotFoundException() {
        when(bookingRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.approve(1L, 404L, true)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_whenRequestedByBooker_returnsBooking() {
        Booking booking = waitingBooking();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.findById(2L, 100L);

        assertThat(result.getId()).isEqualTo(100L);
    }

    @Test
    void findById_whenRequestedByOwner_returnsBooking() {
        Booking booking = waitingBooking();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.findById(1L, 100L);

        assertThat(result.getId()).isEqualTo(100L);
    }

    @Test
    void findById_whenRequestedByStranger_throwsNotFoundException() {
        Booking booking = waitingBooking();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.findById(99L, 100L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_whenBookingMissing_throwsNotFoundException() {
        when(bookingRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findById(1L, 404L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllByBooker_whenStateAll_delegatesToRepository() {
        when(userService.getUserById(2L)).thenReturn(booker);
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L)).thenReturn(List.of(waitingBooking()));

        List<BookingDto> result = List.copyOf(bookingService.findAllByBooker(2L, BookingState.ALL));

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByBooker_whenStateWaiting_delegatesToRepository() {
        when(userService.getUserById(2L)).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(2L, BookingStatus.WAITING))
                .thenReturn(List.of(waitingBooking()));

        List<BookingDto> result = List.copyOf(bookingService.findAllByBooker(2L, BookingState.WAITING));

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByBooker_whenStateRejected_delegatesToRepository() {
        when(userService.getUserById(2L)).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(2L, BookingStatus.REJECTED))
                .thenReturn(List.of());

        assertThat(bookingService.findAllByBooker(2L, BookingState.REJECTED)).isEmpty();
    }

    @Test
    void findAllByBooker_whenStateCurrent_delegatesToRepository() {
        when(userService.getUserById(2L)).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(waitingBooking()));

        List<BookingDto> result = List.copyOf(bookingService.findAllByBooker(2L, BookingState.CURRENT));

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByBooker_whenStatePast_delegatesToRepository() {
        when(userService.getUserById(2L)).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(any(), any()))
                .thenReturn(List.of(waitingBooking()));

        List<BookingDto> result = List.copyOf(bookingService.findAllByBooker(2L, BookingState.PAST));

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByBooker_whenStateFuture_delegatesToRepository() {
        when(userService.getUserById(2L)).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(any(), any()))
                .thenReturn(List.of(waitingBooking()));

        List<BookingDto> result = List.copyOf(bookingService.findAllByBooker(2L, BookingState.FUTURE));

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByOwner_whenStateAll_delegatesToRepository() {
        when(userService.getUserById(1L)).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L)).thenReturn(List.of(waitingBooking()));

        List<BookingDto> result = List.copyOf(bookingService.findAllByOwner(1L, BookingState.ALL));

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByOwner_whenStateWaiting_delegatesToRepository() {
        when(userService.getUserById(1L)).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING))
                .thenReturn(List.of(waitingBooking()));

        List<BookingDto> result = List.copyOf(bookingService.findAllByOwner(1L, BookingState.WAITING));

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByOwner_whenStateRejected_delegatesToRepository() {
        when(userService.getUserById(1L)).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED))
                .thenReturn(List.of());

        assertThat(bookingService.findAllByOwner(1L, BookingState.REJECTED)).isEmpty();
    }

    @Test
    void findAllByOwner_whenStateCurrent_delegatesToRepository() {
        when(userService.getUserById(1L)).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(waitingBooking()));

        List<BookingDto> result = List.copyOf(bookingService.findAllByOwner(1L, BookingState.CURRENT));

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByOwner_whenStatePast_delegatesToRepository() {
        when(userService.getUserById(1L)).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(any(), any()))
                .thenReturn(List.of(waitingBooking()));

        List<BookingDto> result = List.copyOf(bookingService.findAllByOwner(1L, BookingState.PAST));

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByOwner_whenStateFuture_delegatesToRepository() {
        when(userService.getUserById(1L)).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any()))
                .thenReturn(List.of(waitingBooking()));

        List<BookingDto> result = List.copyOf(bookingService.findAllByOwner(1L, BookingState.FUTURE));

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByBooker_whenBookerMissing_propagatesNotFound() {
        when(userService.getUserById(99L)).thenThrow(new NotFoundException("Пользователь с id 99 не найден"));

        assertThatThrownBy(() -> bookingService.findAllByBooker(99L, BookingState.ALL))
                .isInstanceOf(NotFoundException.class);
    }
}
