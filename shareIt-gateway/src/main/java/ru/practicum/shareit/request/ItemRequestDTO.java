package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class ItemRequestDTO {
    private Integer id;
    @NotBlank(message = "description can`t null!")
    private String description;
    private String created;
}
