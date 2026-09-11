package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Ivan", "ivan@mail.ru");
        booker = new User(2L, "Petr", "petr@mail.ru");
        item = new Item(1L, "Дрель", "Простая дрель", true, owner);
    }

    @Test
    void create_whenOwnerExists_savesItem() {
        ItemDto inputDto = new ItemDto(null, "Дрель", "Простая дрель", true);
        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(1L, inputDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void create_whenOwnerMissing_propagatesNotFoundAndDoesNotSave() {
        ItemDto inputDto = new ItemDto(null, "Дрель", "Простая дрель", true);
        when(userService.getUserById(99L)).thenThrow(new NotFoundException("Пользователь с id 99 не найден"));

        assertThatThrownBy(() -> itemService.create(99L, inputDto)).isInstanceOf(NotFoundException.class);

        verify(itemRepository, never()).save(any());
    }

    @Test
    void create_withRequestId_linksItemToRequest() {
        ItemDto inputDto = new ItemDto(null, "Дрель", "Простая дрель", true);
        inputDto.setRequestId(5L);
        ItemRequest request = new ItemRequest();
        request.setId(5L);
        item.setRequest(request);
        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemRequestRepository.findById(5L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(1L, inputDto);

        assertThat(result.getRequestId()).isEqualTo(5L);
    }

    @Test
    void create_whenRequestMissing_throwsNotFoundAndDoesNotSave() {
        ItemDto inputDto = new ItemDto(null, "Дрель", "Простая дрель", true);
        inputDto.setRequestId(404L);
        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemRequestRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.create(1L, inputDto)).isInstanceOf(NotFoundException.class);

        verify(itemRepository, never()).save(any());
    }

    @Test
    void update_whenOwnerMatchesAndAllFieldsProvided_updatesAllFields() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ItemDto patch = new ItemDto(null, "Новая дрель", "Новое описание", false);

        ItemDto result = itemService.update(1L, 1L, patch);

        assertThat(result.getName()).isEqualTo("Новая дрель");
        assertThat(result.getDescription()).isEqualTo("Новое описание");
        assertThat(result.getAvailable()).isFalse();
        verify(userService).getUserById(1L);
    }

    @Test
    void update_withBlankAndNullFields_keepsOriginalValues() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ItemDto patch = new ItemDto(null, "  ", null, null);

        ItemDto result = itemService.update(1L, 1L, patch);

        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Простая дрель");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void update_withNullNameAndBlankDescription_keepsOriginalValues() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ItemDto patch = new ItemDto(null, null, "   ", null);

        ItemDto result = itemService.update(1L, 1L, patch);

        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Простая дрель");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void update_whenItemNotFound_throwsNotFoundException() {
        when(itemRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(1L, 404L, new ItemDto(null, "X", "Y", true)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_whenRequesterIsNotOwner_throwsForbiddenAndDoesNotSave() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(2L, 1L, new ItemDto(null, "Hack", null, null)))
                .isInstanceOf(ForbiddenException.class);

        verify(itemRepository, never()).save(any());
    }

    @Test
    void findById_whenFound_returnsMappedDtoWithComments() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Comment comment = buildComment(item, booker, "Отличная дрель");
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment));

        ItemDto result = itemService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getText()).isEqualTo("Отличная дрель");
        assertThat(result.getComments().get(0).getAuthorName()).isEqualTo("Petr");
    }

    @Test
    void findById_whenMissing_throwsNotFoundException() {
        when(itemRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findById(404L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllByOwner_whenOwnerExists_returnsMappedListWithoutExtras() {
        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(commentRepository.findAllByItemIdIn(List.of(1L))).thenReturn(List.of());
        when(bookingRepository.findByItemIdInAndStatusOrderByStartAsc(List.of(1L), BookingStatus.APPROVED))
                .thenReturn(List.of());

        List<ItemDto> result = List.copyOf(itemService.findAllByOwner(1L));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Дрель");
        assertThat(result.get(0).getComments()).isEmpty();
        assertThat(result.get(0).getLastBooking()).isNull();
        assertThat(result.get(0).getNextBooking()).isNull();
    }

    @Test
    void findAllByOwner_populatesLastAndNextBooking() {
        LocalDateTime now = LocalDateTime.now();
        Booking past = buildBooking(item, booker, now.minusDays(2), now.minusDays(1));
        Booking nextFuture = buildBooking(item, booker, now.plusDays(1), now.plusDays(2));
        Booking laterFuture = buildBooking(item, booker, now.plusDays(3), now.plusDays(4));
        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(commentRepository.findAllByItemIdIn(List.of(1L))).thenReturn(List.of());
        when(bookingRepository.findByItemIdInAndStatusOrderByStartAsc(List.of(1L), BookingStatus.APPROVED))
                .thenReturn(List.of(past, nextFuture, laterFuture));

        List<ItemDto> result = List.copyOf(itemService.findAllByOwner(1L));

        assertThat(result.get(0).getLastBooking().getStart()).isEqualTo(past.getStart());
        assertThat(result.get(0).getNextBooking().getStart()).isEqualTo(nextFuture.getStart());
    }

    @Test
    void findAllByOwner_whenOwnerMissing_propagatesNotFound() {
        when(userService.getUserById(99L)).thenThrow(new NotFoundException("Пользователь с id 99 не найден"));

        assertThatThrownBy(() -> itemService.findAllByOwner(99L)).isInstanceOf(NotFoundException.class);

        verify(itemRepository, never()).findAllByOwnerId(any());
    }

    @Test
    void search_returnsMappedResults() {
        when(itemRepository.search("дрель")).thenReturn(List.of(item));

        List<ItemDto> result = List.copyOf(itemService.search("дрель"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void search_whenNoMatches_returnsEmptyList() {
        when(itemRepository.search("отвертка")).thenReturn(List.of());

        assertThat(itemService.search("отвертка")).isEmpty();
    }

    @Test
    void search_withBlankText_returnsEmptyListWithoutQuerying() {
        assertThat(itemService.search("   ")).isEmpty();

        verify(itemRepository, never()).search(any());
    }

    @Test
    void search_withNullText_returnsEmptyListWithoutQuerying() {
        assertThat(itemService.search(null)).isEmpty();

        verify(itemRepository, never()).search(any());
    }

    @Test
    void search_withLikeWildcardsInText_escapesThemBeforeQuerying() {
        when(itemRepository.search("50\\%\\_off")).thenReturn(List.of());

        itemService.search("50%_off");

        verify(itemRepository).search("50\\%\\_off");
    }

    @Test
    void addComment_whenBookingCompleted_savesComment() {
        when(userService.getUserById(2L)).thenReturn(booker);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                eq(2L), eq(1L), eq(BookingStatus.APPROVED), any())).thenReturn(true);
        Comment saved = buildComment(item, booker, "Спасибо!");
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        CommentDto result = itemService.addComment(2L, 1L, new CommentDto(null, "Спасибо!", null, null));

        assertThat(result.getText()).isEqualTo("Спасибо!");
        assertThat(result.getAuthorName()).isEqualTo("Petr");
    }

    @Test
    void addComment_whenNoCompletedBooking_throwsValidationException() {
        when(userService.getUserById(2L)).thenReturn(booker);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any())).thenReturn(false);

        assertThatThrownBy(() -> itemService.addComment(2L, 1L, new CommentDto(null, "Спасибо!", null, null)))
                .isInstanceOf(ValidationException.class);

        verify(commentRepository, never()).save(any());
    }

    private Comment buildComment(Item forItem, User author, String text) {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText(text);
        comment.setItem(forItem);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    private Booking buildBooking(Item forItem, User forBooker, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(forItem);
        booking.setBooker(forBooker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }
}
