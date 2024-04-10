package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toModel(ItemDTO itemDto);

    ItemDTO toDTO(Item item);

    List<Item> toListModels(List<ItemDTO> itemDtoList);

    List<ItemDTO> toListDTO(List<Item> itemList);
}
