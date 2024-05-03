package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requestor;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setName("John Doe");
        requestor.setEmail("john.doe@example.com");
        userRepository.save(requestor);

        itemRequest = new ItemRequest();
        itemRequest.setRequestor(requestor);
        itemRequest.setDescription("Need a new laptop");
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void whenFindAllByRequestorOrderByCreatedDesc_thenReturnItemRequests() {
        List<ItemRequest> found = itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestor);
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDescription()).isEqualTo("Need a new laptop");
    }

    @Test
    void whenFindAllByRequestorNotOrderByCreatedDesc_thenReturnItemRequests() {
        User otherUser = new User();
        otherUser.setName("Jane Doe");
        otherUser.setEmail("jane.doe@example.com");
        userRepository.save(otherUser);

        Page<ItemRequest> found = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(requestor, PageRequest.of(0, 10, Sort.by("created").descending()));
        assertThat(found.getContent()).isEmpty();

        ItemRequest anotherRequest = new ItemRequest();
        anotherRequest.setRequestor(otherUser);
        anotherRequest.setDescription("Need a camera");
        itemRequestRepository.save(anotherRequest);

        found = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(requestor, PageRequest.of(0, 10, Sort.by("created").descending()));
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getDescription()).isEqualTo("Need a camera");
    }

    @Test
    void whenDeleteById_thenItemRequestShouldBeDeleted() {
        itemRequestRepository.deleteById(itemRequest.getId());
        List<ItemRequest> found = itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestor);
        assertThat(found).isEmpty();
    }
}
