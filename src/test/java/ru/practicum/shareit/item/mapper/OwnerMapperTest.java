package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerMapperTest {
    @InjectMocks
    private OwnerMapper ownerMapper;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldReturnUserWhenIdIsValid() {
        Integer userId = 1;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setName("John Doe");
        expectedUser.setEmail("john.doe@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User result = ownerMapper.fromId(userId);

        assertNotNull(result);
        assertEquals(expectedUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> ownerMapper.fromId(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldReturnNullWhenIdIsNull() {
        assertNull(ownerMapper.fromId(null));
        verify(userRepository, never()).findById(any());
    }
}
