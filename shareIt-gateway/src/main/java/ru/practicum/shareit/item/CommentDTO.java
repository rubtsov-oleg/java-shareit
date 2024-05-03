package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CommentDTO {
    private Integer id;
    @NotBlank(message = "text can`t null!")
    private String text;
    private String authorName;
    private String created;
}
