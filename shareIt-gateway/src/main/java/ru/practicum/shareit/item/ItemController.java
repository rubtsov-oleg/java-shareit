package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.anotation.MarkerOfCreate;
import ru.practicum.shareit.anotation.MarkerOfUpdate;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Collections;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @Validated({MarkerOfCreate.class})
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDTO itemDTO) {
        log.info("Получен запрос Post, на создание вещи: {}", itemDTO.getName());
        return itemClient.saveItem(userId, itemDTO);
    }

    @PatchMapping("/{id}")
    @Validated({MarkerOfUpdate.class})
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody ItemDTO itemDTO, @PathVariable Integer id) {
        log.info("Получен запрос Patch, на обновление вещи: {}", itemDTO.getName());
        return itemClient.updateItem(userId, id, itemDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable Integer id) {
        log.info("Получен запрос GET, на получения вещи, по id: {}", id);
        return itemClient.findById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET, на получения всех вещей пользователя.");
        return itemClient.findAllByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(required = false) String text,
                                         @RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                         @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET, на поиск вещей.");
        if (StringUtils.isBlank(text)) {
            log.info("Поисковый запрос пуст или null.");
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{id}/comment")
    @Validated
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody CommentDTO commentDTO,
                                                @PathVariable Integer id) {
        log.info("Получен запрос Post, на создание комментария.");
        return itemClient.createComment(userId, id, commentDTO);
    }
}
