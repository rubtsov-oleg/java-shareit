package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    public BookingOutDTO saveBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                     @Valid @RequestBody BookingDTO bookingDTO) {
        log.info("Получен запрос Post, на создание бронирования.");
        log.info("Добавлена бронь: {}", bookingDTO.getId());
        return service.saveBooking(userId, bookingDTO);
    }

    @PatchMapping("/{id}")
    public BookingOutDTO updateStatus(@RequestHeader("X-Sharer-User-Id") int userId,
                                      @RequestParam Boolean approved, @PathVariable Integer id) {
        log.info("Получен запрос Patch, на обновление статуса бронирования");
        BookingOutDTO bookingOutDTO = service.updateStatus(userId, id, approved);
        log.info("Обновлен статус: {}", bookingOutDTO.getId());
        return bookingOutDTO;
    }

    @GetMapping("/{id}")
    public BookingOutDTO findById(@RequestHeader("X-Sharer-User-Id") int userId,
                                  @PathVariable Integer id) {
        log.info("Получен запрос GET, на получения вещи, по id: {}", id);
        return service.findById(userId, id);
    }

    @GetMapping
    public List<BookingOutDTO> findByBookerAndState(@RequestHeader("X-Sharer-User-Id") int userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос GET, на получение списка всех бронирований текущего пользователя: {}", userId);
        return service.findByBookerAndState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutDTO> findByOwnerAndState(@RequestHeader("X-Sharer-User-Id") int userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос GET, на получение списка бронирований " +
                "для всех вещей текущего пользователя: {}", userId);
        return service.findByOwnerAndState(userId, state);
    }

}
