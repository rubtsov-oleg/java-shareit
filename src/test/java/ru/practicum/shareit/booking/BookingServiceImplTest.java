package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BookinglValidationException;
import ru.practicum.shareit.exceptions.OwnerValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private Booking booking;
    private BookingDTO bookingDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("John Doe");

        User itemOwner = new User();
        itemOwner.setId(2);
        itemOwner.setName("Jane Doe");

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setOwner(itemOwner);
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);

        bookingDTO = BookingDTO.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void shouldSaveBookingSuccessfully() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingMapper.toModel(any())).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toOutDTO(booking)).thenReturn(BookingOutDTO.builder().build());

        BookingOutDTO result = bookingService.saveBooking(user.getId(), bookingDTO);

        assertNotNull(result);
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowWhenDateUnavailable() {
        LocalDateTime now = LocalDateTime.now();
        bookingDTO = BookingDTO.builder()
                .itemId(item.getId())
                .start(now)
                .end(now)
                .build();

        Exception exception = assertThrows(BookinglValidationException.class,
                () -> bookingService.saveBooking(user.getId(), bookingDTO));
        assertEquals("Одинаковые даты", exception.getMessage());

        bookingDTO = BookingDTO.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        exception = assertThrows(BookinglValidationException.class,
                () -> bookingService.saveBooking(user.getId(), bookingDTO));
        assertEquals("Дата окончания бронирования не может быть раньше старта", exception.getMessage());
    }

    @Test
    void shouldThrowWhenItemUnavailable() {
        item.setAvailable(false);
        when(bookingMapper.toModel(any())).thenReturn(booking);

        Exception exception = assertThrows(BookinglValidationException.class,
                () -> bookingService.saveBooking(user.getId(), bookingDTO));
        assertEquals("Вещь " + item.getId() + " недоступна", exception.getMessage());
    }

    @Test
    void shouldThrowWhenBookingOwnItem() {
        item.setOwner(user);
        when(bookingMapper.toModel(any())).thenReturn(booking);

        Exception exception = assertThrows(OwnerValidationException.class,
                () -> bookingService.saveBooking(user.getId(), bookingDTO));
        assertEquals("Нельзя бронировать свою вещь", exception.getMessage());
    }

    @Test
    void shouldUpdateBookingStatusToApproved() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toOutDTO(any())).thenReturn(BookingOutDTO.builder().build());

        BookingOutDTO result = bookingService.updateStatus(booking.getItem().getOwner().getId(), booking.getId(), true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void shouldRejectBooking() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toOutDTO(any())).thenReturn(BookingOutDTO.builder().build());

        BookingOutDTO result = bookingService.updateStatus(booking.getItem().getOwner().getId(), booking.getId(), false);
        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());

        Exception exception = assertThrows(BookinglValidationException.class,
                () -> bookingService.updateStatus(booking.getItem().getOwner().getId(), booking.getId(), false));
        assertEquals("Уже отменено", exception.getMessage());

        booking.setStatus(BookingStatus.APPROVED);
        exception = assertThrows(BookinglValidationException.class,
                () -> bookingService.updateStatus(booking.getItem().getOwner().getId(), booking.getId(), true));
        assertEquals("Уже одобрено", exception.getMessage());
    }

    @Test
    void shouldThrowWhenNotOwnerTryingToUpdate() {
        User anotherUser = new User();
        anotherUser.setId(3);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        Exception exception = assertThrows(OwnerValidationException.class,
                () -> bookingService.updateStatus(anotherUser.getId(), booking.getId(), true));
        assertTrue(exception.getMessage().contains("не может изменять бронь"));
    }

    @Test
    void shouldFindBookingById() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.toOutDTO(booking)).thenReturn(
                BookingOutDTO.builder()
                        .id(booking.getId())
                        .booker(user)
                        .item(item)
                        .build()
        );

        assertDoesNotThrow(() -> bookingService.findById(user.getId(), booking.getId()));
    }

    @Test
    void shouldThrowWhenUserCannotViewBooking() {
        User anotherUser = new User();
        anotherUser.setId(3);
        booking.setBooker(user);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.toOutDTO(booking)).thenReturn(
                BookingOutDTO.builder()
                        .id(booking.getId())
                        .booker(user)
                        .item(item)
                        .build()
        );

        Exception exception = assertThrows(OwnerValidationException.class,
                () -> bookingService.findById(anotherUser.getId(), booking.getId()));
        assertEquals("Пользователь " + anotherUser.getId() + " не может просматривать бронь " + booking.getId(), exception.getMessage());
    }


    @Test
    void shouldFindByBookerAndState() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerAndState(eq(user.getId()), anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingMapper.toListOutDTO(anyList())).thenReturn(List.of(BookingOutDTO.builder().build()));

        List<BookingOutDTO> results = bookingService.findByBookerAndState(user.getId(), "ALL", 0, 5);

        assertFalse(results.isEmpty());
        verify(bookingRepository).findByBookerAndState(eq(user.getId()), anyString(), any(PageRequest.class));

        Exception exception = assertThrows(BookinglValidationException.class,
                () -> bookingService.findByBookerAndState(user.getId(), "tmp", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void shouldFindByOwnerAndState() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByOwnerAndState(eq(user.getId()), anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingMapper.toListOutDTO(anyList())).thenReturn(List.of(BookingOutDTO.builder().build()));

        List<BookingOutDTO> results = bookingService.findByOwnerAndState(user.getId(), "ALL", 0, 5);

        assertFalse(results.isEmpty());
        verify(bookingRepository).findByOwnerAndState(eq(user.getId()), anyString(), any(PageRequest.class));

        Exception exception = assertThrows(BookinglValidationException.class,
                () -> bookingService.findByOwnerAndState(user.getId(), "tmp", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }
}
