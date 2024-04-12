package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.anotation.MarkerOfCreate;
import ru.practicum.shareit.anotation.MarkerOfUpdate;
import ru.practicum.shareit.item.dto.ItemDTO;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @PostMapping
    @Validated({MarkerOfCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO saveItem(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody ItemDTO itemDTO) {
        log.info("Получен запрос Post, на создание вещи.");
        log.info("Добавлена вещь: {}", itemDTO.getName());
        return service.saveItem(userId, itemDTO);
    }

    @PatchMapping("/{id}")
    @Validated({MarkerOfUpdate.class})
    public ItemDTO updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                              @Valid @RequestBody ItemDTO itemDTO, @PathVariable Integer id) {
        log.info("Получен запрос Patch, на обновление вещи");
        ItemDTO itemDTO1 = service.updateItem(userId, id, itemDTO);
        log.info("Обновлена вещь: {}", itemDTO1.getName());
        return itemDTO1;
    }

    @GetMapping("/{id}")
    public ItemDTO findById(@RequestHeader("X-Sharer-User-Id") int userId,
                            @PathVariable Integer id) {
        log.info("Получен запрос GET, на получения вещи, по id: {}", id);
        return service.findById(id);
    }

    @GetMapping
    public List<ItemDTO> findAllByOwner(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получен запрос GET, на получения всех вещей пользователя.");
        List<ItemDTO> itemDTOList = service.findAllByOwner(userId);
        log.info("Получен ответ, список вещей, размер: {}", itemDTOList.size());
        return itemDTOList;
    }

    @GetMapping("/search")
    public List<ItemDTO> search(@RequestParam(required = false) String text,
                                @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получен запрос GET, на поиск вещей.");
        List<ItemDTO> itemDTOList = service.search(text);
        log.info("Получен ответ, список вещей, размер: {}", itemDTOList.size());
        return itemDTOList;
    }
}
