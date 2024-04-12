package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDTO findById(Integer id);

    List<UserDTO> getAllUsers();

    UserDTO saveUser(UserDTO userDTO);

    void delete(Integer id);

    UserDTO updateUser(Integer id, UserDTO userDTO);
}