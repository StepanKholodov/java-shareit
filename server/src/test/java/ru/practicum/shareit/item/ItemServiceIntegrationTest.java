package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Интеграционный тест на реальной (H2) базе, без транзакции на самом тесте:
 * границу сессии задаёт только {@code @Transactional} сервисных методов.
 * Проверяет, что обращение к ленивым ассоциациям ({@code Item.owner},
 * {@code Booking.item}, {@code Comment.item}) после возврата из репозитория
 * не бросает {@link org.hibernate.LazyInitializationException}.
 */
@SpringBootTest
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User createUser(String email) {
        return userRepository.save(new User(null, "User " + email, email));
    }

    @Test
    void create_savesItemWithOwner() {
        User owner = createUser("isvc-owner-create@mail.ru");

        ItemDto result = itemService.create(owner.getId(), new ItemDto(null, "Дрель", "desc", true));

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Дрель");
    }

    @Test
    void create_withRequestId_doesNotThrowLazyInitializationException() {
        User requestor = createUser("isvc-requestor-create@mail.ru");
        User owner = createUser("isvc-owner-create2@mail.ru");
        ItemRequestDto request = itemRequestService.create(
                requestor.getId(), new ItemRequestDto(null, "Нужна дрель", null, null));

        ItemDto result = itemService.create(owner.getId(),
                new ItemDto(null, "Дрель", "desc", true, null, null, null, request.getId()));

        assertThat(result.getRequestId()).isEqualTo(request.getId());
    }

    @Test
    void update_changesOnlyProvidedFields() {
        User owner = createUser("isvc-owner-update@mail.ru");
        ItemDto created = itemService.create(owner.getId(), new ItemDto(null, "Дрель", "desc", true));

        ItemDto updated = itemService.update(owner.getId(), created.getId(),
                new ItemDto(null, null, null, false));

        assertThat(updated.getName()).isEqualTo("Дрель");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void findById_includesComments_doesNotThrowLazyInitializationException() {
        User owner = createUser("isvc-owner-find@mail.ru");
        User author = createUser("isvc-author-find@mail.ru");
        ItemDto created = itemService.create(owner.getId(), new ItemDto(null, "Дрель", "desc", true));
        bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                itemEntityReference(created.getId()), author, BookingStatus.APPROVED));
        itemService.addComment(author.getId(), created.getId(), new CommentDto(null, "Отличная дрель", null, null));

        ItemDto result = itemService.findById(created.getId());

        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getAuthorName()).isEqualTo(author.getName());
    }

    @Test
    void findAllByOwner_includesLastBookingAndComments_doesNotThrowLazyInitializationException() {
        User owner = createUser("isvc-owner-list@mail.ru");
        User booker = createUser("isvc-booker-list@mail.ru");
        ItemDto created = itemService.create(owner.getId(), new ItemDto(null, "Дрель", "desc", true));
        Item itemRef = itemEntityReference(created.getId());
        bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                itemRef, booker, BookingStatus.APPROVED));
        itemService.addComment(booker.getId(), created.getId(), new CommentDto(null, "Отличная дрель", null, null));

        List<ItemDto> result = List.copyOf(itemService.findAllByOwner(owner.getId()));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastBooking()).isNotNull();
        assertThat(result.get(0).getLastBooking().getBookerId()).isEqualTo(booker.getId());
        assertThat(result.get(0).getComments()).hasSize(1);
    }

    @Test
    void search_returnsOnlyAvailableMatches() {
        User owner = createUser("isvc-owner-search@mail.ru");
        itemService.create(owner.getId(), new ItemDto(null, "Уникальная-дрель-search", "desc", true));
        itemService.create(owner.getId(), new ItemDto(null, "Уникальная-дрель-search-2", "desc", false));

        List<ItemDto> result = List.copyOf(itemService.search("Уникальная-дрель-search"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Уникальная-дрель-search");
    }

    @Test
    void addComment_afterCompletedBooking_savesComment() {
        User owner = createUser("isvc-owner-comment@mail.ru");
        User booker = createUser("isvc-booker-comment@mail.ru");
        ItemDto created = itemService.create(owner.getId(), new ItemDto(null, "Дрель", "desc", true));
        bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                itemEntityReference(created.getId()), booker, BookingStatus.APPROVED));

        CommentDto result = itemService.addComment(booker.getId(), created.getId(),
                new CommentDto(null, "Отличная дрель", null, null));

        assertThat(result.getId()).isNotNull();
        assertThat(result.getAuthorName()).isEqualTo(booker.getName());
    }

    @Test
    void addComment_withoutCompletedBooking_throwsValidation() {
        User owner = createUser("isvc-owner-nocomment@mail.ru");
        User stranger = createUser("isvc-stranger-nocomment@mail.ru");
        ItemDto created = itemService.create(owner.getId(), new ItemDto(null, "Дрель", "desc", true));

        assertThatThrownBy(() -> itemService.addComment(stranger.getId(), created.getId(),
                new CommentDto(null, "Отличная дрель", null, null)))
                .isInstanceOf(ValidationException.class);
    }

    private Item itemEntityReference(Long itemId) {
        Item item = new Item();
        item.setId(itemId);
        return item;
    }
}
