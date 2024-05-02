package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO saveItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDTO itemDTO) {
        log.info("Получен запрос Post, на создание вещи.");
        log.info("Добавлена вещь: {}", itemDTO.getName());
        return service.saveItem(userId, itemDTO);
    }

    @PatchMapping("/{id}")
    public ItemDTO updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                              @RequestBody ItemDTO itemDTO, @PathVariable Integer id) {
        log.info("Получен запрос Patch, на обновление вещи");
        ItemDTO itemDTO1 = service.updateItem(userId, id, itemDTO);
        log.info("Обновлена вещь: {}", itemDTO1.getName());
        return itemDTO1;
    }

    @GetMapping("/{id}")
    public ItemOutDTO findById(@RequestHeader("X-Sharer-User-Id") int userId,
                               @PathVariable Integer id) {
        log.info("Получен запрос GET, на получения вещи, по id: {}", id);
        return service.findById(userId, id);
    }

    @GetMapping
    public List<ItemOutDTO> findAllByOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                           @RequestParam Integer from,
                                           @RequestParam Integer size) {
        log.info("Получен запрос GET, на получения всех вещей пользователя.");
        List<ItemOutDTO> itemDTOList = service.findAllByOwner(userId, from, size);
        log.info("Получен ответ, список вещей, размер: {}", itemDTOList.size());
        return itemDTOList;
    }

    @GetMapping("/search")
    public List<ItemOutDTO> search(@RequestParam(required = false) String text,
                                   @RequestHeader("X-Sharer-User-Id") int userId,
                                   @RequestParam Integer from,
                                   @RequestParam Integer size) {
        log.info("Получен запрос GET, на поиск вещей.");
        List<ItemOutDTO> itemDTOList = service.search(text, from, size);
        log.info("Получен ответ, список вещей, размер: {}", itemDTOList.size());
        return itemDTOList;
    }

    @PostMapping("/{id}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDTO createComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                    @RequestBody CommentDTO commentDTO,
                                    @PathVariable Integer id) {
        log.info("Получен запрос Post, на создание комментария.");
        return service.createComment(userId, id, commentDTO);
    }
}
