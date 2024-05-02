package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDTO itemRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Need a new laptop");
        itemRequest.setRequestor(user);

        itemRequestDTO = ItemRequestDTO.builder()
                .id(1)
                .description("Need a new laptop")
                .build();
    }

    @Test
    void saveItemRequest() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRequestMapper.toDTO(any(ItemRequest.class))).thenReturn(itemRequestDTO);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDTO result = itemRequestService.saveItemRequest(1, itemRequestDTO);

        assertEquals(itemRequestDTO, result);
        verify(userRepository).findById(1);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void findAllByRequestor() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorOrderByCreatedDesc(user)).thenReturn(Collections.singletonList(itemRequest));
        when(itemRequestMapper.toListDTO(anyList())).thenReturn(Collections.singletonList(itemRequestDTO));

        List<ItemRequestDTO> result = itemRequestService.findAllByRequestor(1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(itemRequestDTO, result.get(0));
    }

    @Test
    void findAll() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(user, PageRequest.of(0, 10))).thenReturn(new PageImpl<>(Collections.singletonList(itemRequest)));
        when(itemRequestMapper.toListDTO(anyList())).thenReturn(Collections.singletonList(itemRequestDTO));

        List<ItemRequestDTO> result = itemRequestService.findAll(1, 0, 10);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(itemRequestDTO, result.get(0));
    }

    @Test
    void findById() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toListDTO(anyList())).thenReturn(Collections.singletonList(itemRequestDTO));

        ItemRequestDTO result = itemRequestService.findById(1, 1);

        assertNotNull(result);
        assertEquals(itemRequestDTO, result);
    }

    @Test
    void findById_NotFound() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(itemRequestRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemRequestService.findById(1, 1));
    }

    @Test
    void userNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemRequestService.saveItemRequest(1, itemRequestDTO));
        assertThrows(NoSuchElementException.class, () -> itemRequestService.findAllByRequestor(1));
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        Integer nonExistentUserId = 999;
        Integer requestId = 1;
        when(userRepository.existsById(nonExistentUserId)).thenReturn(false);

        assertThrows(NoSuchElementException.class,
                () -> itemRequestService.findById(nonExistentUserId, requestId),
                "Пользователь " + nonExistentUserId + " не найден");
        verify(userRepository).existsById(nonExistentUserId);
    }

    @Test
    void shouldEnrichItemRequestsWithItemsCorrectly() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1);
        itemRequest1.setDescription("Need a laptop");

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2);
        itemRequest2.setDescription("Need a camera");

        Item item1 = new Item();
        item1.setId(1);
        item1.setName("Laptop");
        item1.setRequest(itemRequest1);

        Item item2 = new Item();
        item2.setId(2);
        item2.setName("Camera");
        item2.setRequest(itemRequest2);

        List<ItemRequest> requests = Arrays.asList(itemRequest1, itemRequest2);
        List<Item> items = Arrays.asList(item1, item2);

        ItemShortDTO itemShortDTO1 = ItemShortDTO.builder().id(1).name("Laptop").build();

        ItemShortDTO itemShortDTO2 = ItemShortDTO.builder().id(2).name("Camera").build();

        ItemRequestDTO itemRequestDTO1 = ItemRequestDTO.builder()
                .id(1)
                .description("Need a laptop")
                .items(new ArrayList<>())
                .build();

        ItemRequestDTO itemRequestDTO2 = ItemRequestDTO.builder()
                .id(2)
                .description("Need a camera")
                .items(new ArrayList<>())
                .build();

        when(itemRepository.findAllByRequestIn(requests)).thenReturn(items);
        when(itemRequestMapper.toListDTO(requests)).thenReturn(Arrays.asList(itemRequestDTO1, itemRequestDTO2));
        when(itemMapper.toShortDTO(item1)).thenReturn(itemShortDTO1);
        when(itemMapper.toShortDTO(item2)).thenReturn(itemShortDTO2);

        List<ItemRequestDTO> enrichedRequests = itemRequestService.itemsEnrichment(requests);

        assertNotNull(enrichedRequests);
        assertEquals(2, enrichedRequests.size());
        assertEquals(1, enrichedRequests.get(0).getItems().size());
        assertEquals(1, enrichedRequests.get(1).getItems().size());
        assertEquals("Laptop", enrichedRequests.get(0).getItems().get(0).getName());
        assertEquals("Camera", enrichedRequests.get(1).getItems().get(0).getName());
    }


}
