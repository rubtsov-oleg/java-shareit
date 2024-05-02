package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;

import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class RequestMapper {
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequest fromId(Integer id) {
        return id != null ? itemRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Запрос " + id + " не найден")) : null;
    }
}
