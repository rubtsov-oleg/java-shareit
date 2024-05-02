package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.item.dto.ItemShortDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {
    @Mock
    private OwnerMapper ownerMapper;

    @Mock
    private RequestMapper requestMapper;
    @InjectMocks
    private ItemMapperImpl itemMapper;

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertNull(itemMapper.toModel(null));
        assertNull(itemMapper.toDTO(null));
        assertNull(itemMapper.toShortDTO(null));
        assertNull(itemMapper.toListShortDTO(null));
        assertNull(itemMapper.toOutDTO(null));
        assertNull(itemMapper.toListOutDTO(null));
        assertNull(itemMapper.toListModels(null));
        assertNull(itemMapper.toListDTO(null));
    }

    @Test
    void shouldMapItemToItemDTO() {
        Item item = new Item();
        item.setId(1);
        item.setName("Book");
        item.setDescription("A wonderful story");
        item.setAvailable(true);

        User owner = new User();
        owner.setId(1);
        owner.setName("John Doe");
        owner.setEmail("john.doe@example.com");
        item.setOwner(owner);

        ItemDTO itemDTO = itemMapper.toDTO(item);

        assertNotNull(itemDTO);
        assertEquals(item.getId(), itemDTO.getId());
        assertEquals(item.getName(), itemDTO.getName());
        assertEquals(item.getDescription(), itemDTO.getDescription());
        assertEquals(owner.getId(), itemDTO.getOwnerId());

        owner.setId(null);
        item.setOwner(owner);
        assertNull(itemMapper.toDTO(null));
        assertNull(itemMapper.toDTO(item).getOwnerId());

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(null);
        item.setRequest(itemRequest);
        assertNull(itemMapper.toDTO(item).getRequestId());

        itemRequest.setId(999);
        item.setRequest(itemRequest);
        assertEquals(itemMapper.toDTO(item).getRequestId(), 999);
    }

    @Test
    void shouldMapItemToItemOutDTO() {
        Item item = new Item();
        item.setId(1);
        item.setName("Book");
        item.setDescription("A wonderful story");
        item.setAvailable(true);

        ItemOutDTO itemOutDTO = itemMapper.toOutDTO(item);

        assertNotNull(itemOutDTO);
        assertEquals(item.getId(), itemOutDTO.getId());
        assertEquals(item.getName(), itemOutDTO.getName());
        assertEquals(item.getDescription(), itemOutDTO.getDescription());
        assertNull(itemOutDTO.getNextBooking());
        assertNull(itemOutDTO.getLastBooking());
        assertNull(itemOutDTO.getComments());
    }

    @Test
    void shouldMapItemListToItemOutDTOList() {
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("Book 1");
        item1.setDescription("Story 1");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(2);
        item2.setName("Book 2");
        item2.setDescription("Story 2");
        item2.setAvailable(true);

        List<Item> itemList = Arrays.asList(item1, item2);
        List<ItemOutDTO> itemOutDTOList = itemMapper.toListOutDTO(itemList);

        assertNotNull(itemOutDTOList);
        assertEquals(2, itemOutDTOList.size());
        assertEquals(item1.getId(), itemOutDTOList.get(0).getId());
        assertEquals(item2.getId(), itemOutDTOList.get(1).getId());
    }

    @Test
    void shouldMapItemListToItemDTOList() {
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("Book 1");
        item1.setDescription("Story 1");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(2);
        item2.setName("Book 2");
        item2.setDescription("Story 2");
        item2.setAvailable(true);

        List<Item> itemList = Arrays.asList(item1, item2);
        List<ItemDTO> itemDTOList = itemMapper.toListDTO(itemList);

        assertNotNull(itemDTOList);
        assertEquals(2, itemDTOList.size());
        assertEquals(item1.getId(), itemDTOList.get(0).getId());
        assertEquals(item2.getId(), itemDTOList.get(1).getId());
    }

    @Test
    void shouldMapItemToItemShortDTO() {
        Item item = new Item();
        item.setId(1);
        item.setName("Book");
        item.setDescription("A wonderful story");
        item.setAvailable(true);

        ItemShortDTO itemShortDTO = itemMapper.toShortDTO(item);

        assertNotNull(itemShortDTO);
        assertEquals(item.getId(), itemShortDTO.getId());
        assertEquals(item.getName(), itemShortDTO.getName());
    }

    @Test
    void shouldMapItemListToItemShortDTOList() {
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("Book 1");
        item1.setDescription("Story 1");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(2);
        item2.setName("Book 2");
        item2.setDescription("Story 2");
        item2.setAvailable(true);

        List<Item> itemList = Arrays.asList(item1, item2);
        List<ItemShortDTO> itemShortDTOList = itemMapper.toListShortDTO(itemList);

        assertNotNull(itemShortDTOList);
        assertEquals(2, itemShortDTOList.size());
        assertEquals(item1.getId(), itemShortDTOList.get(0).getId());
        assertEquals(item2.getId(), itemShortDTOList.get(1).getId());
    }

    @Test
    void shouldMapItemDTOListToItemList() {
        ItemDTO itemDTO1 = ItemDTO.builder().id(1).name("Book 1").description("Story 1").ownerId(1).build();
        ItemDTO itemDTO2 = ItemDTO.builder().id(2).name("Book 2").description("Story 2").ownerId(2).build();
        List<ItemDTO> itemDTOList = Arrays.asList(itemDTO1, itemDTO2);

        User user1 = new User();
        user1.setId(1);
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");

        User user2 = new User();
        user2.setId(2);
        user2.setName("Jane Doe");
        user2.setEmail("jane.doe@example.com");

        when(ownerMapper.fromId(1)).thenReturn(user1);
        when(ownerMapper.fromId(2)).thenReturn(user2);

        List<Item> itemList = itemMapper.toListModels(itemDTOList);

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
        assertEquals(itemDTO1.getId(), itemList.get(0).getId());
        assertEquals(itemDTO2.getId(), itemList.get(1).getId());
    }
}
