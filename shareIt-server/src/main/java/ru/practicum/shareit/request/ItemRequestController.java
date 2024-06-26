package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDTO saveItemRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                          @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("Получен запрос Post, на создание запроса на вещи.");
        log.info("Добавлен запрос: {}", itemRequestDTO.getDescription());
        return service.saveItemRequest(userId, itemRequestDTO);
    }

    @GetMapping
    public List<ItemRequestDTO> findAllByRequestor(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получен запрос GET, на получение всех запросов пользователя.");
        List<ItemRequestDTO> itemRequestDTOList = service.findAllByRequestor(userId);
        log.info("Получен ответ, список запросов, размер: {}", itemRequestDTOList.size());
        return itemRequestDTOList;
    }

    @GetMapping("/all")
    public List<ItemRequestDTO> findAll(@RequestHeader("X-Sharer-User-Id") int userId,
                                        @RequestParam Integer from,
                                        @RequestParam Integer size) {
        log.info("Получен запрос GET, на получение всех запросов.");
        List<ItemRequestDTO> itemRequestDTOList = service.findAll(userId, from, size);
        log.info("Получен ответ, список запросов, размер: {}", itemRequestDTOList.size());
        return itemRequestDTOList;
    }

    @GetMapping("/{id}")
    public ItemRequestDTO findById(@RequestHeader("X-Sharer-User-Id") int userId,
                                   @PathVariable Integer id) {
        log.info("Получен запрос GET, на получения запроса, по id: {}", id);
        return service.findById(userId, id);
    }
}
