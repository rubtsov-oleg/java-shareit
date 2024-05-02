package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemShortDTO;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
public class ItemRequestDTO {
    private Integer id;
    @NotBlank(message = "description can`t null!")
    private String description;
    private String created;
    private List<ItemShortDTO> items;
}
