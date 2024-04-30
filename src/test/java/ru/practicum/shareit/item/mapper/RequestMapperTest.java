package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestMapperTest {
    @InjectMocks
    private RequestMapper requestMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    void shouldReturnItemRequestWhenIdIsValid() {
        Integer requestId = 1;
        ItemRequest expectedRequest = new ItemRequest();
        expectedRequest.setId(requestId);
        expectedRequest.setDescription("Need a laptop");

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(expectedRequest));

        ItemRequest result = requestMapper.fromId(requestId);

        assertNotNull(result);
        assertEquals(expectedRequest, result);
        verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void shouldThrowExceptionWhenItemRequestNotFound() {
        Integer requestId = 999;
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> requestMapper.fromId(requestId));
        verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void shouldReturnNullWhenIdIsNull() {
        assertNull(requestMapper.fromId(null));
        verify(itemRequestRepository, never()).findById(any());
    }
}

