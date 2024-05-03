package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BookingTest {

    @Test
    void testGettersAndSetters() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        User booker = new User();
        booker.setId(1);
        booking.setBooker(booker);
        Item item = new Item();
        item.setId(1);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        assertEquals(1, booking.getId());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        assertEquals(booker, booking.getBooker());
        assertEquals(item, booking.getItem());
        assertEquals(LocalDateTime.now().toLocalDate(), booking.getStart().toLocalDate());
        assertEquals(LocalDateTime.now().plusDays(1).toLocalDate(), booking.getEnd().toLocalDate());
    }

    @Test
    void testEquals() {
        Booking booking1 = new Booking();
        booking1.setId(1);
        Booking booking2 = new Booking();
        booking2.setId(1);
        Booking booking3 = new Booking();
        booking3.setId(2);

        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
    }

    @Test
    void testHashCode() {
        Booking booking1 = new Booking();
        booking1.setId(1);
        Booking booking2 = new Booking();
        booking2.setId(1);

        assertEquals(booking1.hashCode(), booking2.hashCode());
    }

    @Test
    void testToString() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        User booker = new User();
        booker.setId(1);
        booking.setBooker(booker);
        Item item = new Item();
        item.setId(1);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        String expected = "Booking(id=1, start=" + booking.getStart() + ", end=" + booking.getEnd() + ", item=" + item + ", booker=" + booker + ", status=APPROVED)";
        assertEquals(expected, booking.toString());
    }
}
