package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testGettersAndSetters() {
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

        ItemRequest request = new ItemRequest();
        request.setId(1);
        request.setRequestor(owner);
        item.setRequest(request);

        assertEquals(1, item.getId());
        assertEquals("Book", item.getName());
        assertEquals("A wonderful story", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void testEquals() {
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("Book");
        item1.setDescription("A wonderful story");
        item1.setAvailable(true);

        User owner1 = new User();
        owner1.setId(1);
        owner1.setName("John Doe");
        owner1.setEmail("john.doe@example.com");
        item1.setOwner(owner1);

        ItemRequest request1 = new ItemRequest();
        request1.setId(1);
        request1.setRequestor(owner1);
        item1.setRequest(request1);

        Item item2 = new Item();
        item2.setId(1);
        item2.setName("Book");
        item2.setDescription("A wonderful story");
        item2.setAvailable(true);

        User owner2 = new User();
        owner2.setId(1);
        owner2.setName("John Doe");
        owner2.setEmail("john.doe@example.com");
        item2.setOwner(owner2);

        ItemRequest request2 = new ItemRequest();
        request2.setId(1);
        request2.setRequestor(owner2);
        item2.setRequest(request2);

        Item item3 = new Item();
        item3.setId(2);
        item3.setName("Laptop");
        item3.setDescription("High-performance laptop");
        item3.setAvailable(false);

        User owner3 = new User();
        owner3.setId(2);
        owner3.setName("Jane Doe");
        owner3.setEmail("jane.doe@example.com");
        item3.setOwner(owner3);

        ItemRequest request3 = new ItemRequest();
        request3.setId(2);
        request3.setRequestor(owner3);
        item3.setRequest(request3);

        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
    }

    @Test
    void testHashCode() {
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("Book");
        item1.setDescription("A wonderful story");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(1);
        item2.setName("Book");
        item2.setDescription("A wonderful story");
        item2.setAvailable(true);

        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testToString() {
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

        String expected = "Item(id=1, name=Book, description=A wonderful story, available=true, owner=User(id=1, name=John Doe, email=john.doe@example.com), request=null)";
        assertEquals(expected, item.toString());
    }
}
