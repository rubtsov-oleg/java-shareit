package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemRequestMapperTest {
    private ItemRequestMapper itemRequestMapper;

    @BeforeEach
    void setup() {
        itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertNull(itemRequestMapper.toDTO(null));
        assertNull(itemRequestMapper.toListDTO(null));
    }

    @Test
    void shouldMapItemRequestToItemRequestDTO() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Need a camera");
        itemRequest.setCreated(Instant.parse("2022-03-25T14:00:00Z"));

        ItemRequestDTO itemRequestDTO = itemRequestMapper.toDTO(itemRequest);

        assertNotNull(itemRequestDTO);
        assertEquals(itemRequest.getId(), itemRequestDTO.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDTO.getDescription());
        assertNull(itemRequestDTO.getItems());
    }

    @Test
    void shouldMapListItemRequestToListItemRequestDTO() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1);
        itemRequest1.setDescription("Need a camera");
        itemRequest1.setCreated(Instant.parse("2022-03-25T14:00:00Z"));

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2);
        itemRequest2.setDescription("Looking for a bike");
        itemRequest2.setCreated(Instant.parse("2022-03-26T15:00:00Z"));

        List<ItemRequest> itemRequests = Arrays.asList(itemRequest1, itemRequest2);

        List<ItemRequestDTO> itemRequestDTOs = itemRequestMapper.toListDTO(itemRequests);

        assertNotNull(itemRequestDTOs);
        assertEquals(2, itemRequestDTOs.size());
        assertEquals(itemRequests.get(0).getId(), itemRequestDTOs.get(0).getId());
        assertEquals(itemRequests.get(1).getId(), itemRequestDTOs.get(1).getId());
        assertNull(itemRequestDTOs.get(0).getItems());
        assertNull(itemRequestDTOs.get(1).getItems());
    }
}
