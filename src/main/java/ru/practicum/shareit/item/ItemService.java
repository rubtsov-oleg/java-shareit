package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.List;

public interface ItemService {
    ItemDTO saveItem(Integer userId, ItemDTO itemDTO);

    ItemDTO updateItem(Integer userId, Integer id, ItemDTO itemDTO);

    ItemDTO findById(Integer id);

    List<ItemDTO> findAllByOwner(Integer id);

    List<ItemDTO> search(String text);
}
