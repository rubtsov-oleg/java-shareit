package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    void testGettersAndSetters() {
        ItemRequest itemRequest = new ItemRequest();
        User requestor = new User();
        requestor.setId(1);
        requestor.setName("John Doe");
        requestor.setEmail("john.doe@example.com");

        itemRequest.setId(100);
        itemRequest.setDescription("Need a new laptop");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(Instant.parse("2022-03-25T14:00:00Z"));

        assertEquals(100, itemRequest.getId());
        assertEquals("Need a new laptop", itemRequest.getDescription());
        assertEquals(requestor, itemRequest.getRequestor());
        assertEquals(Instant.parse("2022-03-25T14:00:00Z"), itemRequest.getCreated());
    }

    @Test
    void testEquals() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(100);
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(100);
        ItemRequest itemRequest3 = new ItemRequest();
        itemRequest3.setId(101);

        assertEquals(itemRequest1, itemRequest2);
        assertNotEquals(itemRequest1, itemRequest3);
    }

    @Test
    void testHashCode() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(100);
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(100);

        assertEquals(itemRequest1.hashCode(), itemRequest2.hashCode());
    }

    @Test
    void testToString() {
        ItemRequest itemRequest = new ItemRequest();
        User requestor = new User();
        requestor.setId(1);
        requestor.setName("John Doe");
        requestor.setEmail("john.doe@example.com");

        itemRequest.setId(100);
        itemRequest.setDescription("Need a new laptop");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(Instant.parse("2022-03-25T14:00:00Z"));

        String expected = "ItemRequest(id=100, description=Need a new laptop, created=2022-03-25T14:00:00Z)";
        assertEquals(expected, itemRequest.toString());
    }
}
