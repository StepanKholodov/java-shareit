package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA-репозиторий бронирований.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);

    List<Booking> findByItemIdInAndStatusOrderByStartAsc(List<Long> itemIds, BookingStatus status);
}
