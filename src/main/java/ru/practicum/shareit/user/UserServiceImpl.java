package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EmailValidationException;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDTO findById(Integer id) {
        return userMapper.toDTO(userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь " + id + " не найден")));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.toListDTO(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {
        try {
            return userMapper.toDTO(userRepository.save(userMapper.toModel(userDTO)));
        } catch (DataIntegrityViolationException e) {
            throw new EmailValidationException(userDTO.getEmail() + " данная почта уже используется");
        }
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        UserDTO existedUser = findById(id);
        if (userDTO.getEmail() != null) {
            existedUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getName() != null) {
            existedUser.setName(userDTO.getName());
        }
        return userMapper.toDTO(userRepository.save(userMapper.toModel(existedUser)));
    }
}
