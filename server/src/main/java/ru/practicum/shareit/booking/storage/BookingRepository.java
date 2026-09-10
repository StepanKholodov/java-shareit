package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA-репозиторий бронирований. Списочные методы жадно подгружают
 * {@code item}/{@code booker} через {@link EntityGraph}, чтобы
 * {@code BookingMapper.toBookingDto} не делал по лишнему запросу на
 * каждое бронирование при обращении к {@code item.getName()}.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);

    List<Booking> findByItemIdInAndStatusOrderByStartAsc(List<Long> itemIds, BookingStatus status);
}
