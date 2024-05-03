package ru.practicum.shareit.booking.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class ItemMapper {
    private final ItemRepository itemRepository;

    public Item fromId(Integer id) {
        return id != null ? itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Вещь " + id + " не найдена")) : null;
    }
}
