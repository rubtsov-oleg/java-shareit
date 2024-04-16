package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    @Mapping(target = "items", ignore = true)
    ItemRequestDTO toDTO(ItemRequest itemRequest);

    List<ItemRequestDTO> toListDTO(List<ItemRequest> itemRequestList);
}
