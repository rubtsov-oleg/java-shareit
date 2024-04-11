package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final UserMapper userMapper;

    @Override
    public UserDTO findById(Integer id) {
        return userMapper.toDTO(userDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь " + id + " не найден")));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.toListDTO(userDAO.findAll());
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        return userMapper.toDTO(userDAO.save(userMapper.toModel(userDTO)));
    }

    @Override
    public void delete(Integer id) {
        boolean deleted = userDAO.delete(id);
        if (!deleted) {
            throw new NoSuchElementException("Пользователь " + id + " не найден");
        }
    }

    @Override
    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        UserDTO existedUser = findById(id);
        if (userDTO.getEmail() != null) {
            existedUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getName() != null) {
            existedUser.setName(userDTO.getName());
        }
        return userMapper.toDTO(userDAO.update(userMapper.toModel(existedUser)));
    }
}
