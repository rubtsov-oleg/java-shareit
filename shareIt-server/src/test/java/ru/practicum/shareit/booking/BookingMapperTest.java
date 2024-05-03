package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;
import ru.practicum.shareit.booking.mapper.BookerMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.mapper.ItemMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {
    @InjectMocks
    private BookingMapperImpl bookingMapper;

    @Mock
    private BookerMapper bookerMapper;

    @Mock
    private ItemMapper itemMapper;

    @Test
    void shouldMapBookingToBookingDTO() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        Item item = new Item();
        item.setId(1);
        booking.setItem(item);
        User booker = new User();
        booker.setId(1);
        booking.setBooker(booker);

        BookingDTO bookingDTO = bookingMapper.toDTO(booking);

        assertNotNull(bookingDTO);
        assertEquals(booking.getId(), bookingDTO.getId());
        assertEquals(booking.getItem().getId(), bookingDTO.getItemId());
        assertEquals(booking.getBooker().getId(), bookingDTO.getBookerId());
    }

    @Test
    void shouldMapBookingDTOToBooking() {
        when(bookerMapper.fromId(anyInt())).thenReturn(new User());
        when(itemMapper.fromId(anyInt())).thenReturn(new Item());

        BookingDTO bookingDTO = BookingDTO.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1)
                .bookerId(1)
                .build();

        Booking booking = bookingMapper.toModel(bookingDTO);

        assertNotNull(booking);
        assertEquals(bookingDTO.getId(), booking.getId());
        assertNotNull(booking.getItem());
        assertNotNull(booking.getBooker());
    }

    @Test
    void shouldMapBookingToBookingOutDTO() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        Item item = new Item();
        item.setId(1);
        booking.setItem(item);

        User booker = new User();
        booker.setId(1);
        booking.setBooker(booker);

        BookingOutDTO bookingOutDTO = bookingMapper.toOutDTO(booking);

        assertNotNull(bookingOutDTO);
        assertEquals(booking.getId(), bookingOutDTO.getId());
        assertNotNull(bookingOutDTO.getItem());
        assertEquals(booking.getItem().getId(), bookingOutDTO.getItem().getId());
        assertNotNull(bookingOutDTO.getBooker());
        assertEquals(booking.getBooker().getId(), bookingOutDTO.getBooker().getId());
    }

    @Test
    void shouldMapListBookingToListBookingDTO() {
        Booking booking1 = new Booking();
        booking1.setId(1);
        Booking booking2 = new Booking();
        booking2.setId(2);
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingDTO> bookingDTOs = bookingMapper.toListDTO(bookings);

        assertNotNull(bookingDTOs);
        assertEquals(2, bookingDTOs.size());
        assertEquals(booking1.getId(), bookingDTOs.get(0).getId());
        assertEquals(booking2.getId(), bookingDTOs.get(1).getId());
    }

    @Test
    void shouldMapListBookingDTOToListBooking() {
        BookingDTO bookingDTO1 = BookingDTO.builder()
                .id(1)
                .build();
        BookingDTO bookingDTO2 = BookingDTO.builder()
                .id(2)
                .build();
        List<BookingDTO> bookingDTOs = Arrays.asList(bookingDTO1, bookingDTO2);

        List<Booking> bookings = bookingMapper.toListModels(bookingDTOs);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(bookingDTO1.getId(), bookings.get(0).getId());
        assertEquals(bookingDTO2.getId(), bookings.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertNull(bookingMapper.toModel(null));
        assertNull(bookingMapper.toDTO(null));
        assertNull(bookingMapper.toOutDTO(null));
        assertNull(bookingMapper.toListModels(null));
        assertNull(bookingMapper.toListDTO(null));
        assertNull(bookingMapper.toListOutDTO(null));
    }
}
