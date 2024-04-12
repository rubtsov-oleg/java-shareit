package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDAO {
    Item save(Item item);

    Item update(Item item);

    Optional<Item> findById(Integer id);

    List<Item> findAllByOwner(Integer id);

    List<Item> search(String text);
}
