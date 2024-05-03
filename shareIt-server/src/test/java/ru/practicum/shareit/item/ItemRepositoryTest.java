package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        userRepository.save(user);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Need a drill");
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful and reliable drill.");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(itemRequest);
        itemRepository.save(item);
    }

    @Test
    void whenFindByOwner_thenItemsShouldBeReturned() {
        Page<Item> items = itemRepository.findAllByOwnerOrderByIdAsc(user, PageRequest.of(0, 10));
        assertThat(items).isNotNull();
        assertThat(items.getTotalElements()).isEqualTo(1);
        assertEquals("Drill", items.getContent().get(0).getName());
    }

    @Test
    void whenSearchByText_thenMatchingItemsShouldBeReturned() {
        String searchQuery = "drill";
        Page<Item> items = itemRepository.search(searchQuery, PageRequest.of(0, 10));
        assertThat(items).isNotNull();
        assertThat(items.getTotalElements()).isGreaterThan(0);
        assertEquals("Drill", items.getContent().get(0).getName());
    }

    @Test
    void whenFindAllByRequestIn_thenItemsShouldBeReturned() {
        List<Item> foundItems = itemRepository.findAllByRequestIn(List.of(itemRequest));
        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems.get(0).getName()).isEqualTo("Drill");
    }
}
