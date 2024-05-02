package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @Validated
    public ResponseEntity<Object> saveItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @Valid @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("Получен запрос Post, на создание запроса на вещи: {}", itemRequestDTO.getDescription());
        return itemRequestClient.saveItemRequest(userId, itemRequestDTO);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET, на получение всех запросов пользователя: {}", userId);
        return itemRequestClient.findAllByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET, на получение всех запросов.");
        return itemRequestClient.findAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable Integer id) {
        log.info("Получен запрос GET, на получения запроса, по id: {}", id);
        return itemRequestClient.findById(userId, id);
    }
}
