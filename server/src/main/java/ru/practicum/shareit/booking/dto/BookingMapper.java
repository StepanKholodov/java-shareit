package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

/**
 * Преобразование между моделью {@link Booking} и его DTO-представлениями.
 */
public final class BookingMapper {

    private BookingMapper() {
    }

    /**
     * @param booking сущность бронирования
     * @return DTO для отдачи через REST API
     */
    public static BookingDto toBookingDto(Booking booking) {
        BookingItemDto itemDto = new BookingItemDto(booking.getItem().getId(), booking.getItem().getName());
        BookingBookerDto bookerDto = new BookingBookerDto(booking.getBooker().getId());
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getStatus(), itemDto, bookerDto);
    }

    /**
     * @param requestDto запрос на создание бронирования
     * @param item       бронируемая вещь
     * @param booker     пользователь, создающий бронирование
     * @return новая сущность бронирования в статусе {@link BookingStatus#WAITING}
     */
    public static Booking toBooking(BookItemRequestDto requestDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(requestDto.getStart());
        booking.setEnd(requestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    /**
     * @param booking сущность бронирования
     * @return краткое представление для встраивания в {@code ItemDto}
     */
    public static ItemBookingDto toItemBookingDto(Booking booking) {
        return new ItemBookingDto(booking.getId(), booking.getBooker().getId(), booking.getStart(), booking.getEnd());
    }
}
