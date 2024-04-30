package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BookinglValidationException;
import ru.practicum.shareit.exceptions.OwnerValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingOutDTO saveBooking(Integer userId, BookingDTO bookingDTO) {
        if (bookingDTO.getEnd().isBefore(bookingDTO.getStart())) {
            throw new BookinglValidationException("Дата окончания бронирования не может быть раньше старта");
        } else if (bookingDTO.getEnd().equals(bookingDTO.getStart())) {
            throw new BookinglValidationException("Одинаковые даты");
        }
        Booking booking = bookingMapper.toModel(bookingDTO);
        if (booking.getItem().getAvailable().equals(false)) {
            throw new BookinglValidationException("Вещь " + booking.getItem().getId() + " недоступна");
        } else if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new OwnerValidationException("Нельзя бронировать свою вещь");
        }
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь " + userId + " не найден"));
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.toOutDTO(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingOutDTO updateStatus(Integer userId, Integer bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Бронь " + bookingId + " не найдена"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new OwnerValidationException("Пользователь " + userId + " не может изменять бронь " + bookingId);
        }
        if (approved.equals(true)) {
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new BookinglValidationException("Уже одобрено");
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else if (approved.equals(false)) {
            if (booking.getStatus().equals(BookingStatus.REJECTED)) {
                throw new BookinglValidationException("Уже отменено");
            }
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toOutDTO(bookingRepository.save(booking));
    }

    @Override
    public BookingOutDTO findById(Integer userId, Integer bookingId) {
        BookingOutDTO bookingOutDTO = bookingMapper.toOutDTO(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Бронь " + bookingId + " не найдена")));
        if (!(bookingOutDTO.getBooker().getId().equals(userId) ||
                bookingOutDTO.getItem().getOwner().getId().equals(userId))) {
            throw new OwnerValidationException("Пользователь " + userId + " не может просматривать бронь " + bookingId);
        }
        return bookingOutDTO;
    }

    @Override
    public List<BookingOutDTO> findByBookerAndState(Integer userId, String state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь " + userId + " не найден"));
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookinglValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingMapper.toListOutDTO(
                bookingRepository
                        .findByBookerAndState(userId, state, PageRequest.of(from / size, size)).getContent());
    }

    @Override
    public List<BookingOutDTO> findByOwnerAndState(Integer userId, String state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь " + userId + " не найден"));
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookinglValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingMapper.toListOutDTO(
                bookingRepository
                        .findByOwnerAndState(userId, state, PageRequest.of(from / size, size)).getContent());
    }
}
