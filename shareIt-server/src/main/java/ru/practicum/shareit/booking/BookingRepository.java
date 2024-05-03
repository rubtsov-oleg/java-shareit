package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND " +
            "(:state = 'ALL' OR " +
            "(:state = 'CURRENT' AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP) OR " +
            "(:state = 'PAST' AND b.end < CURRENT_TIMESTAMP) OR " +
            "(:state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP) OR " +
            "(:state = 'WAITING' AND b.status = ru.practicum.shareit.booking.model.BookingStatus.WAITING) OR " +
            "(:state = 'REJECTED' AND b.status = ru.practicum.shareit.booking.model.BookingStatus.REJECTED)) " +
            "ORDER BY b.end DESC ")
    Page<Booking> findByBookerAndState(@Param("userId") int userId, @Param("state") String state, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND " +
            "(:state = 'ALL' OR " +
            "(:state = 'CURRENT' AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP) OR " +
            "(:state = 'PAST' AND b.end < CURRENT_TIMESTAMP) OR " +
            "(:state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP) OR " +
            "(:state = 'WAITING' AND b.status = ru.practicum.shareit.booking.model.BookingStatus.WAITING) OR " +
            "(:state = 'REJECTED' AND b.status = ru.practicum.shareit.booking.model.BookingStatus.REJECTED)) " +
            "ORDER BY b.start DESC")
    Page<Booking> findByOwnerAndState(@Param("userId") int userId, @Param("state") String state, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item IN :items " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC")
    List<Booking> findNextBookingsByItems(@Param("items") List<Item> items);

    @Query("SELECT b FROM Booking b WHERE b.item IN :items " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.end DESC ")
    List<Booking> findLastBookingsByItems(@Param("items") List<Item> items);

    Optional<Booking> findFirstByItemAndStartBeforeOrderByEndDescIdDesc(Item item, LocalDateTime currentTime);

    Optional<Booking> findFirstByItemAndStartAfterAndStatusNotOrderByStartAscIdAsc(
            Item item, LocalDateTime currentTime, BookingStatus status
    );

    Optional<Booking> findFirstByBookerAndItemAndStatusNotAndEndBeforeOrderByEndDesc(
            User booker, Item item, BookingStatus status, LocalDateTime currentTime
    );
}
