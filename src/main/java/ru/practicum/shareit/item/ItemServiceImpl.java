package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.OwnerValidationException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.user.UserDAO;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDAO itemDAO;
    private final UserDAO userDAO;
    private final ItemMapper itemMapper;

    @Override
    public ItemDTO saveItem(Integer userId, ItemDTO itemDTO) {
        existUser(userId);
        itemDTO.setOwner(userId);
        return itemMapper.toDTO(itemDAO.save(itemMapper.toModel(itemDTO)));
    }

    @Override
    public ItemDTO findById(Integer id) {
        return itemMapper.toDTO(itemDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Вещь " + id + " не найден")));
    }

    @Override
    public ItemDTO updateItem(Integer userId, Integer id, ItemDTO itemDTO) {
        ItemDTO existedItem = findById(id);
        if (!Objects.equals(existedItem.getOwner(), userId)) {
            throw new OwnerValidationException("Пользователь " + userId + " не может изменять вещь " + id);
        }
        if (itemDTO.getName() != null) {
            existedItem.setName(itemDTO.getName());
        }
        if (itemDTO.getDescription() != null) {
            existedItem.setDescription(itemDTO.getDescription());
        }
        if (itemDTO.getAvailable() != null) {
            existedItem.setAvailable(itemDTO.getAvailable());
        }
        return itemMapper.toDTO(itemDAO.update(itemMapper.toModel(existedItem)));
    }

    @Override
    public List<ItemDTO> findAllByOwner(Integer id) {
        return itemMapper.toListDTO(itemDAO.findAllByOwner(id));
    }

    @Override
    public List<ItemDTO> search(String text) {
        return itemMapper.toListDTO(itemDAO.search(text));
    }

    private void existUser(Integer userId) {
        boolean isExistUser = userDAO.isExistById(userId);
        if (!isExistUser) {
            throw new NoSuchElementException("Пользователь " + userId + " не найден");
        }
    }
}
