package ru.practicum.shareit.user;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toModel(UserDTO userDTO);

    UserDTO toDTO(User user);

    List<User> toListModels(List<UserDTO> userDTOList);

    List<UserDTO> toListDTO(List<User> userList);
}
