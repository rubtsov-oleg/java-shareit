package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        assertEquals(1, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void testEquals() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");

        User user2 = new User();
        user2.setId(1);
        user2.setName("John Doe");
        user2.setEmail("john.doe@example.com");

        User user3 = new User();
        user3.setId(2);
        user3.setName("Jane Doe");
        user3.setEmail("jane.doe@example.com");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
    }

    @Test
    void testHashCode() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");

        User user2 = new User();
        user2.setId(1);
        user2.setName("John Doe");
        user2.setEmail("john.doe@example.com");

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        String expected = "User(id=1, name=John Doe, email=john.doe@example.com)";
        assertEquals(expected, user.toString());
    }
}
