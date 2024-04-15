package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;

import java.util.List;

public interface BookingService {
    BookingOutDTO saveBooking(Integer userId, BookingDTO bookingDTO);

    BookingOutDTO updateStatus(Integer userId, Integer bookingId, Boolean approved);

    BookingOutDTO findById(Integer userId, Integer bookingId);

    List<BookingOutDTO> findByBookerAndState(Integer userId, String state);

    List<BookingOutDTO> findByOwnerAndState(Integer userId, String state);
}
