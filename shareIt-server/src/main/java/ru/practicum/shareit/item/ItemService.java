package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;

import java.util.List;

public interface ItemService {
    ItemDTO saveItem(Integer userId, ItemDTO itemDTO);

    ItemDTO updateItem(Integer userId, Integer id, ItemDTO itemDTO);

    ItemOutDTO findById(Integer userId, Integer id);

    List<ItemOutDTO> findAllByOwner(Integer id, Integer from, Integer size);

    List<ItemOutDTO> search(String text, Integer from, Integer size);

    CommentDTO createComment(Integer userId, Integer itemId, CommentDTO commentDTO);
}
