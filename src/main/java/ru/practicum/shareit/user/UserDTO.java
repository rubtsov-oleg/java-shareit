package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.anotation.MarkerOfCreate;
import ru.practicum.shareit.anotation.MarkerOfUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
public class UserDTO {
    @Null(groups = MarkerOfCreate.class)
    @Null(groups = MarkerOfUpdate.class)
    private Integer id;
    @NotBlank(groups = MarkerOfCreate.class, message = "name not null.")
    private String name;
    @NotNull(groups = MarkerOfCreate.class, message = "ID can`t null!")
    @Email(groups = MarkerOfCreate.class, message = "Incorrect Email.")
    private String email;
}
