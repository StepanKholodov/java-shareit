package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Интеграционный тест на реальной (H2) базе, без транзакции на самом тесте:
 * границу сессии задаёт только {@code @Transactional} сервисных методов.
 * Так проверяется, что обращение к ленивым ассоциациям ({@code Booking.item},
 * {@code Booking.item.owner}) после возврата из репозитория не бросает
 * {@link org.hibernate.LazyInitializationException}.
 */
@SpringBootTest
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User createUser(String email) {
        User user = new User(null, "User " + email, email);
        return userRepository.save(user);
    }

    private Item createItem(User owner) {
        Item item = new Item(null, "Дрель", "desc", true, owner);
        return itemRepository.save(item);
    }

    @Test
    void create_doesNotThrowLazyInitializationException() {
        User owner = createUser("owner-create@mail.ru");
        User booker = createUser("booker-create@mail.ru");
        Item item = createItem(owner);
        BookItemRequestDto requestDto = new BookItemRequestDto(
                item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.create(booker.getId(), requestDto);

        assertThat(result.getItem().getId()).isEqualTo(item.getId());
        assertThat(result.getItem().getName()).isEqualTo("Дрель");
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void approve_doesNotThrowLazyInitializationException() {
        User owner = createUser("owner-approve@mail.ru");
        User booker = createUser("booker-approve@mail.ru");
        Item item = createItem(owner);
        BookingDto created = bookingService.create(booker.getId(), new BookItemRequestDto(
                item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        BookingDto[] approved = new BookingDto[1];
        assertThatCode(() -> approved[0] = bookingService.approve(owner.getId(), created.getId(), true))
                .doesNotThrowAnyException();

        assertThat(approved[0].getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(approved[0].getItem().getName()).isEqualTo("Дрель");
    }

    @Test
    void findById_doesNotThrowLazyInitializationException() {
        User owner = createUser("owner-find@mail.ru");
        User booker = createUser("booker-find@mail.ru");
        Item item = createItem(owner);
        BookingDto created = bookingService.create(booker.getId(), new BookItemRequestDto(
                item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        BookingDto result = bookingService.findById(owner.getId(), created.getId());

        assertThat(result.getItem().getName()).isEqualTo("Дрель");
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void findAllByOwner_doesNotThrowLazyInitializationException() {
        User owner = createUser("owner-list@mail.ru");
        User booker = createUser("booker-list@mail.ru");
        Item item = createItem(owner);
        bookingService.create(booker.getId(), new BookItemRequestDto(
                item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        List<BookingDto> result = List.copyOf(bookingService.findAllByOwner(owner.getId(), BookingState.ALL));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItem().getName()).isEqualTo("Дрель");
    }
}
