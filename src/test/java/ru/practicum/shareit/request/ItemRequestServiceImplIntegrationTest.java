package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        entityManager.persist(user);
        entityManager.flush();

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Need a laptop");
        itemRequest.setRequestor(user);
        entityManager.persist(itemRequest);
        entityManager.flush();
    }

    @Test
    void shouldSaveItemRequest() {
        ItemRequestDTO newItemRequestDTO = ItemRequestDTO.builder()
                .description("Need a camera")
                .build();
        ItemRequestDTO savedItemRequest = itemRequestService.saveItemRequest(user.getId(), newItemRequestDTO);
        assertThat(savedItemRequest).isNotNull();
        assertThat(savedItemRequest.getDescription()).isEqualTo("Need a camera");
    }

    @Test
    void shouldFindAllByRequestor() {
        List<ItemRequestDTO> foundRequests = itemRequestService.findAllByRequestor(user.getId());
        assertThat(foundRequests).isNotEmpty();
        assertThat(foundRequests.get(0).getDescription()).isEqualTo(itemRequest.getDescription());
    }

    @Test
    void shouldFindById() {
        ItemRequestDTO foundRequest = itemRequestService.findById(user.getId(), itemRequest.getId());
        assertThat(foundRequest.getDescription()).isEqualTo(itemRequest.getDescription());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        assertThrows(NoSuchElementException.class, () -> itemRequestService.findAllByRequestor(999));
    }

    @Test
    void shouldThrowWhenItemRequestNotFound() {
        assertThrows(NoSuchElementException.class, () -> itemRequestService.findById(user.getId(), 999));
    }
}
