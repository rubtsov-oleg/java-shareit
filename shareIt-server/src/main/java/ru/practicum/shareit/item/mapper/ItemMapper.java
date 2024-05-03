package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.item.dto.ItemShortDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OwnerMapper.class, RequestMapper.class})
public interface ItemMapper {
    @Mapping(source = "ownerId", target = "owner")
    @Mapping(source = "requestId", target = "request")
    Item toModel(ItemDTO itemDto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "request.id", target = "requestId")
    ItemDTO toDTO(Item item);

    @Mapping(source = "request.id", target = "requestId")
    ItemShortDTO toShortDTO(Item item);

    List<ItemShortDTO> toListShortDTO(List<Item> itemList);

    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ItemOutDTO toOutDTO(Item item);

    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    List<ItemOutDTO> toListOutDTO(List<Item> itemList);

    List<Item> toListModels(List<ItemDTO> itemDtoList);

    List<ItemDTO> toListDTO(List<Item> itemList);
}
