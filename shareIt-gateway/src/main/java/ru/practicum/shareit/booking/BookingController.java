package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BookinglValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @Validated
    public ResponseEntity<Object> saveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @Valid @RequestBody BookingDTO bookingDTO) {
        log.info("Получен запрос Post, на создание бронирования: {}", bookingDTO.getItemId());
        if (bookingDTO.getEnd().isBefore(bookingDTO.getStart())) {
            throw new BookinglValidationException("Дата окончания бронирования не может быть раньше старта");
        } else if (bookingDTO.getEnd().equals(bookingDTO.getStart())) {
            throw new BookinglValidationException("Одинаковые даты");
        }
        return bookingClient.saveBooking(userId, bookingDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam Boolean approved, @PathVariable Integer id) {
        log.info("Получен запрос Patch, на обновление статуса бронирования: {}", id);
        return bookingClient.updateStatus(userId, id, approved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable Integer id) {
        log.info("Получен запрос GET, на получения вещи, по id: {}", id);
        return bookingClient.findById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> findByBookerAndState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                       @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET, на получение списка всех бронирований текущего пользователя: {}", userId);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.findByBookerAndState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwnerAndState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                      @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET, на получение списка бронирований " +
                "для всех вещей текущего пользователя: {}", userId);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.findByOwnerAndState(userId, state, from, size);
    }

}
