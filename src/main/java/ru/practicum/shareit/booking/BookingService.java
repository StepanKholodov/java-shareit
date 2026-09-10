package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

/**
 * Бизнес-логика работы с бронированиями.
 */
public interface BookingService {

    /**
     * Создаёт запрос на бронирование вещи в статусе {@link BookingStatus#WAITING}.
     *
     * @param bookerId  id пользователя, создающего бронирование
     * @param requestDto данные бронирования (вещь, даты)
     * @return созданное бронирование
     * @throws ru.practicum.shareit.exception.NotFoundException   если пользователь или вещь не найдены,
     *                                                             либо владелец пытается забронировать свою вещь
     * @throws ru.practicum.shareit.exception.ValidationException если вещь недоступна либо даты некорректны
     */
    BookingDto create(Long bookerId, BookItemRequestDto requestDto);

    /**
     * Подтверждает или отклоняет запрос на бронирование. Может быть выполнено только владельцем вещи.
     *
     * @param ownerId   id пользователя, выполняющего запрос
     * @param bookingId id бронирования
     * @param approved  {@code true} — подтвердить, {@code false} — отклонить
     * @return бронирование с обновлённым статусом
     * @throws ru.practicum.shareit.exception.NotFoundException   если бронирование не найдено
     * @throws ru.practicum.shareit.exception.ForbiddenException  если запрос выполняет не владелец вещи
     * @throws ru.practicum.shareit.exception.ValidationException если статус бронирования уже не {@code WAITING}
     */
    BookingDto approve(Long ownerId, Long bookingId, boolean approved);

    /**
     * Возвращает бронирование по id. Доступно только автору бронирования или владельцу вещи.
     *
     * @param userId    id пользователя, выполняющего запрос
     * @param bookingId id бронирования
     * @return найденное бронирование
     * @throws ru.practicum.shareit.exception.NotFoundException если бронирование не найдено,
     *                                                           либо запрос выполняет посторонний пользователь
     */
    BookingDto findById(Long userId, Long bookingId);

    /**
     * Возвращает бронирования текущего пользователя как арендатора, отфильтрованные по состоянию.
     *
     * @param bookerId id пользователя-арендатора
     * @param state    фильтр состояния
     * @return бронирования, отсортированные от новых к старым
     */
    Collection<BookingDto> findAllByBooker(Long bookerId, BookingState state);

    /**
     * Возвращает бронирования всех вещей текущего пользователя-владельца, отфильтрованные по состоянию.
     *
     * @param ownerId id пользователя-владельца
     * @param state   фильтр состояния
     * @return бронирования, отсортированные от новых к старым
     */
    Collection<BookingDto> findAllByOwner(Long ownerId, BookingState state);
}
