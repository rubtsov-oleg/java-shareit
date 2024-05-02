package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDTO;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDTO saveItemRequest(Integer userId, ItemRequestDTO itemRequestDTO);

    List<ItemRequestDTO> findAllByRequestor(Integer userId);

    List<ItemRequestDTO> findAll(Integer userId, Integer from, Integer size);

    ItemRequestDTO findById(Integer userId, Integer id);
}
