package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDTO saveItemRequest(Integer userId, ItemRequestDTO itemRequestDTO) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь " + userId + " не найден"));
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDTO.getDescription());
        itemRequest.setRequestor(requestor);
        return itemRequestMapper.toDTO(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDTO> findAllByRequestor(Integer userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь " + userId + " не найден"));
        return itemsEnrichment(itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestor));
    }

    @Override
    public List<ItemRequestDTO> findAll(Integer userId, Integer from, Integer size) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь " + userId + " не найден"));
        return itemsEnrichment(
                itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(
                        requestor, PageRequest.of(from / size, size)).getContent()
        );
    }

    @Override
    public ItemRequestDTO findById(Integer userId, Integer id) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("Пользователь " + userId + " не найден");
        }
        return itemsEnrichment(
                Collections.singletonList(
                        itemRequestRepository
                                .findById(id).orElseThrow(() -> new NoSuchElementException("Запрос " + id + " не найден"))
                )
        ).get(0);
    }

    public List<ItemRequestDTO> itemsEnrichment(List<ItemRequest> itemRequestList) {
        List<ItemRequestDTO> itemRequestDTOList = itemRequestMapper.toListDTO(itemRequestList);
        List<Item> items = itemRepository.findAllByRequestIn(itemRequestList);
        for (ItemRequestDTO itemRequestDTO : itemRequestDTOList) {
            List<ItemShortDTO> itemShortDTOList = new ArrayList<>();
            for (Item item : items) {
                if (item.getRequest().getId().equals(itemRequestDTO.getId())) {
                    itemShortDTOList.add(itemMapper.toShortDTO(item));
                }
            }
            itemRequestDTO.setItems(itemShortDTOList);
        }
        return itemRequestDTOList;
    }
}
