package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BookinglValidationException;
import ru.practicum.shareit.exceptions.OwnerValidationException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemMapper itemMapper;

    private User user;
    private ItemDTO item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        userRepository.save(user);

        ItemDTO itemDTO = ItemDTO.builder()
                .name("Drill")
                .description("Powerful tool")
                .available(true)
                .build();
        item = itemService.saveItem(user.getId(), itemDTO);
    }

    @Test
    void shouldCreateItem() {
        assertThat(item).isNotNull();
        assertThat(item.getName()).isEqualTo("Drill");
        assertThat(item.getOwnerId()).isEqualTo(user.getId());
    }

    @Test
    void shouldFindItemById() {
        ItemOutDTO foundItem = itemService.findById(user.getId(), item.getId());
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getName()).isEqualTo(item.getName());
    }

    @Test
    void shouldUpdateItem() {
        ItemDTO updateDTO = ItemDTO.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();
        ItemDTO updatedItem = itemService.updateItem(user.getId(), item.getId(), updateDTO);

        assertThat(updatedItem.getName()).isEqualTo("Updated Name");
        assertThat(updatedItem.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void shouldNotUpdateItemIfUserIsNotOwner() {
        User anotherUser = new User();
        anotherUser.setName("Jane Doe");
        anotherUser.setEmail("jane@example.com");
        userRepository.save(anotherUser);

        ItemDTO updateDTO = ItemDTO.builder()
                .name("Illegal Update")
                .description("Should Fail")
                .available(false)
                .build();

        assertThrows(OwnerValidationException.class, () -> {
            itemService.updateItem(anotherUser.getId(), item.getId(), updateDTO);
        });
    }

    @Test
    void shouldAddCommentToItem() {
        Booking booking = new Booking();
        booking.setItem(itemMapper.toModel(item));
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDTO commentDTO = CommentDTO.builder()
                .text("Great tool!")
                .build();

        CommentDTO addedComment = itemService.createComment(user.getId(), item.getId(), commentDTO);

        assertThat(addedComment.getText()).isEqualTo("Great tool!");
        assertThat(addedComment.getAuthorName()).isEqualTo(user.getName());
    }

    @Test
    void shouldNotAddCommentIfUserHasNotBooked() {
        User anotherUser = new User();
        anotherUser.setName("Jake");
        anotherUser.setEmail("jake@example.com");
        userRepository.save(anotherUser);

        CommentDTO commentDTO = CommentDTO.builder()
                .text("Failed comment")
                .build();

        assertThrows(BookinglValidationException.class, () -> {
            itemService.createComment(anotherUser.getId(), item.getId(), commentDTO);
        });
    }
}

