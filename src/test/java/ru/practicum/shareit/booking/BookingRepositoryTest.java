package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        userRepository.save(user);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
    }

    @Test
    void whenFindByBookerAndState_thenSuccess() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findByBookerAndState(user.getId(), "ALL", pageRequest).getContent();

        assertFalse(bookings.isEmpty());
        assertEquals(bookings.get(0).getBooker().getId(), user.getId());
    }

    @Test
    void whenFindByOwnerAndState_thenSuccess() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findByOwnerAndState(user.getId(), "ALL", pageRequest).getContent();

        assertFalse(bookings.isEmpty());
        assertEquals(bookings.get(0).getItem().getOwner().getId(), user.getId());
    }

    @Test
    void whenFindNextBookingsByItems_thenSuccess() {
        List<Booking> nextBookings = bookingRepository.findNextBookingsByItems(Arrays.asList(item));

        assertFalse(nextBookings.isEmpty());
        assertTrue(nextBookings.stream().allMatch(b -> b.getStart().isAfter(LocalDateTime.now())));
    }

    @Test
    void whenFindLastBookingsByItems_thenSuccess() {
        List<Booking> lastBookings = bookingRepository.findLastBookingsByItems(Arrays.asList(item));

        assertTrue(lastBookings.isEmpty());
    }

    @Test
    void whenFindFirstByBookerAndItemAndStatusNotAndEndBeforeOrderByEndDesc_thenSuccess() {
        Optional<Booking> foundBooking = bookingRepository.findFirstByBookerAndItemAndStatusNotAndEndBeforeOrderByEndDesc(
                user, item, BookingStatus.REJECTED, LocalDateTime.now().plusDays(3));

        assertTrue(foundBooking.isPresent());
        assertEquals(foundBooking.get().getId(), booking.getId());
    }
}
