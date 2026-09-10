package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void toBookingDto_mapsAllFields() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        User booker = new User(2L, "Petr", "petr@mail.ru");
        Item item = new Item(10L, "Дрель", "desc", true, owner);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(5L, start, end, item, booker, BookingStatus.WAITING);

        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getItem().getId()).isEqualTo(10L);
        assertThat(dto.getItem().getName()).isEqualTo("Дрель");
        assertThat(dto.getBooker().getId()).isEqualTo(2L);
    }

    @Test
    void toBooking_buildsWaitingBooking() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        User booker = new User(2L, "Petr", "petr@mail.ru");
        Item item = new Item(10L, "Дрель", "desc", true, owner);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookItemRequestDto requestDto = new BookItemRequestDto(10L, start, end);

        Booking booking = BookingMapper.toBooking(requestDto, item, booker);

        assertThat(booking.getStart()).isEqualTo(start);
        assertThat(booking.getEnd()).isEqualTo(end);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void toItemBookingDto_mapsAllFields() {
        User owner = new User(1L, "Ivan", "ivan@mail.ru");
        User booker = new User(2L, "Petr", "petr@mail.ru");
        Item item = new Item(10L, "Дрель", "desc", true, owner);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(5L, start, end, item, booker, BookingStatus.APPROVED);

        ItemBookingDto dto = BookingMapper.toItemBookingDto(booking);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getBookerId()).isEqualTo(2L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }

    @Test
    void constructor_isPrivate() throws Exception {
        Constructor<BookingMapper> constructor = BookingMapper.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
