package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BookinglValidationException;
import ru.practicum.shareit.exceptions.OwnerValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @PersistenceContext
    private EntityManager em;

    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        em.persist(user);

        owner = new User();
        owner.setName("Jane Roe");
        owner.setEmail("jane@example.com");
        em.persist(owner);

        item = new Item();
        item.setName("Drill");
        item.setDescription("A powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);
        em.flush();
    }

    @Test
    void shouldSaveBooking() {
        BookingDTO bookingDTO = BookingDTO.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingOutDTO result = bookingService.saveBooking(user.getId(), bookingDTO);

        assertNotNull(result);
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void shouldThrowWhenBookingUnavailableItem() {
        item.setAvailable(false);
        em.persist(item);

        BookingDTO bookingDTO = BookingDTO.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(BookinglValidationException.class, () -> bookingService.saveBooking(user.getId(), bookingDTO));
    }

    @Test
    void shouldThrowWhenBookingOwnItem() {
        BookingDTO bookingDTO = BookingDTO.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(OwnerValidationException.class, () -> bookingService.saveBooking(owner.getId(), bookingDTO));
    }

    @Test
    void shouldApproveBooking() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        BookingOutDTO result = bookingService.updateStatus(owner.getId(), booking.getId(), true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void shouldRejectBooking() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        BookingOutDTO result = bookingService.updateStatus(owner.getId(), booking.getId(), false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }
}

