package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.EmailValidationException;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");

        userDTO = UserDTO.builder()
                .id(1)
                .email("test@example.com")
                .name("Test User")
                .build();
    }

    @Test
    void findById() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.findById(1);

        assertEquals(userDTO, result);
        verify(userRepository).findById(1);
        verify(userMapper).toDTO(user);
    }

    @Test
    void findById_NotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.findById(1));
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userMapper.toListDTO(anyList())).thenReturn(Arrays.asList(userDTO));

        List<UserDTO> result = userService.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(userDTO, result.get(0));
    }

    @Test
    void saveUser() {
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toModel(any(UserDTO.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        UserDTO result = userService.saveUser(userDTO);

        assertEquals(userDTO, result);
        verify(userRepository).save(user);
    }

    @Test
    void saveUser_EmailExists() {
        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(EmailValidationException.class, () -> userService.saveUser(userDTO));
    }

    @Test
    void delete() {
        userService.delete(1);

        verify(userRepository).deleteById(1);
    }

    @Test
    void updateUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(any())).thenReturn(userDTO);
        when(userMapper.toModel(any(UserDTO.class))).thenReturn(user);

        UserDTO updatedUser = userService.updateUser(1, userDTO);

        assertNotNull(updatedUser);
        verify(userRepository).save(user);
    }
}
