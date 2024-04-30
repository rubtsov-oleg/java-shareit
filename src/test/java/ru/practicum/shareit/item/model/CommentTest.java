package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void testGettersAndSetters() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setCreated(Instant.parse("2024-04-29T12:00:00Z"));

        Item item = new Item();
        item.setId(1);
        item.setName("Book");
        comment.setItem(item);

        User author = new User();
        author.setId(1);
        author.setName("John Doe");
        author.setEmail("john.doe@example.com");
        comment.setAuthor(author);

        assertEquals(1, comment.getId());
        assertEquals("Great item!", comment.getText());
        assertEquals(Instant.parse("2024-04-29T12:00:00Z"), comment.getCreated());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
    }

    @Test
    void testEquals() {
        Comment comment1 = new Comment();
        comment1.setId(1);
        comment1.setText("Great item!");

        Comment comment2 = new Comment();
        comment2.setId(1);
        comment2.setText("Great item!");

        Comment comment3 = new Comment();
        comment3.setId(2);
        comment3.setText("Awesome!");

        assertEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
    }

    @Test
    void testHashCode() {
        Comment comment1 = new Comment();
        comment1.setId(1);
        comment1.setText("Great item!");

        Comment comment2 = new Comment();
        comment2.setId(1);
        comment2.setText("Great item!");

        assertEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    void testToString() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setCreated(Instant.parse("2024-04-29T12:00:00Z"));

        String expected = "Comment(id=1, text=Great item!, item=null, author=null, created=2024-04-29T12:00:00Z)";
        assertEquals(expected, comment.toString());
    }
}
