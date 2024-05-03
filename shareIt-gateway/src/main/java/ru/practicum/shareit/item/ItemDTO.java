package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.anotation.MarkerOfCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDTO {
    @NotBlank(groups = MarkerOfCreate.class, message = "Name can`t null!")
    private String name;
    @NotNull(groups = MarkerOfCreate.class, message = "description can`t null!")
    @Size(groups = MarkerOfCreate.class, min = 1, max = 200, message = "Max size 200!")
    private String description;
    @NotNull(groups = MarkerOfCreate.class, message = "available can`t null!")
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
}
