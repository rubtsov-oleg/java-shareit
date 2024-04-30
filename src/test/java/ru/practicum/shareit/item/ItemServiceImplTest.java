package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BookinglValidationException;
import ru.practicum.shareit.exceptions.OwnerValidationException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private ItemDTO itemDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("John Doe");

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful tool");
        item.setAvailable(true);
        item.setOwner(user);

        itemDTO = ItemDTO.builder().id(1).name("Drill").description("Powerful tool").available(true).ownerId(1).build();
    }

    @Test
    void saveItem() {
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.toModel(any(ItemDTO.class))).thenReturn(item);
        when(itemMapper.toDTO(any(Item.class))).thenReturn(itemDTO);

        ItemDTO result = itemService.saveItem(user.getId(), itemDTO);

        assertNotNull(result);
        assertEquals(itemDTO.getName(), result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void findById() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemMapper.toOutDTO(any(Item.class))).thenReturn(ItemOutDTO.builder().build());

        assertDoesNotThrow(() -> itemService.findById(1, 1));
    }

    @Test
    void updateItem() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemMapper.toDTO(any(Item.class))).thenReturn(itemDTO);
        when(itemMapper.toModel(any(ItemDTO.class))).thenReturn(item);
        doReturn(item).when(itemRepository).save(any(Item.class));

        ItemDTO result = itemService.updateItem(1, 1, itemDTO);

        assertNotNull(result);
        assertEquals(itemDTO.getName(), result.getName());
        verify(itemRepository).save(any(Item.class));
    }


    @Test
    void findAllByOwner() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerOrderByIdAsc(eq(user), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(item)));
        when(itemMapper.toOutDTO(any(Item.class))).thenReturn(ItemOutDTO.builder().build());

        List<ItemOutDTO> results = itemService.findAllByOwner(1, 0, 10);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void search() {
        when(itemRepository.search(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(item)));
        when(itemMapper.toOutDTO(any(Item.class))).thenReturn(ItemOutDTO.builder().build());

        List<ItemOutDTO> results = itemService.search("drill", 0, 10);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void createComment() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByBookerAndItemAndStatusNotAndEndBeforeOrderByEndDesc(any(), any(), any(), any()))
                .thenReturn(Optional.of(new Booking()));
        when(commentRepository.save(any())).thenReturn(new Comment());

        assertDoesNotThrow(() -> itemService.createComment(1, 1, CommentDTO.builder().build()));
    }

    @Test
    void findById_ItemNotFound_ThrowsNoSuchElementException() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemService.findById(1, 9999),
                "Expected findById to throw, but it didn't");
    }

    @Test
    void findById_ItemFound_DoesNotThrowException() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemMapper.toOutDTO(any(Item.class))).thenReturn(ItemOutDTO.builder().build());

        assertDoesNotThrow(() -> itemService.findById(1, 1),
                "Expected findById not to throw, but it did");
    }

    @Test
    void updateItem_ShouldThrowException_WhenUserIsNotOwner() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemMapper.toDTO(any(Item.class))).thenReturn(itemDTO);
        itemDTO.setOwnerId(2);

        assertThrows(OwnerValidationException.class, () -> itemService.updateItem(1, 1, itemDTO));
    }

    @Test
    void search_ShouldReturnEmptyList_WhenTextIsBlank() {
        List<ItemOutDTO> results = itemService.search(" ", 0, 10);
        assertTrue(results.isEmpty());
    }

    @Test
    void bookingEnrichment_ShouldSetNextAndLastBooking() {
        BookingDTO nextBookingDTO = BookingDTO.builder().itemId(1).build();
        BookingDTO lastBookingDTO = BookingDTO.builder().itemId(1).build();
        ItemOutDTO mockOutDTO = ItemOutDTO.builder().id(1).build();

        when(itemMapper.toOutDTO(any(Item.class))).thenReturn(mockOutDTO);
        when(bookingMapper.toListDTO(any())).thenReturn(Arrays.asList(nextBookingDTO), Arrays.asList(lastBookingDTO));

        List<ItemOutDTO> results = itemService.bookingEnrichment(Collections.singletonList(item));
        assertNotNull(results.get(0).getNextBooking());
        assertNotNull(results.get(0).getLastBooking());
        assertEquals(nextBookingDTO, results.get(0).getNextBooking());
        assertEquals(lastBookingDTO, results.get(0).getLastBooking());
    }


    @Test
    void createComment_ShouldThrowException_WhenUserHasNotBookedItem() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findFirstByBookerAndItemAndStatusNotAndEndBeforeOrderByEndDesc(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(BookinglValidationException.class, () -> itemService.createComment(1, 1, CommentDTO.builder().build()));
    }

}

