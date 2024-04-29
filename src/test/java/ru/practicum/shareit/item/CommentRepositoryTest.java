package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private Comment comment;
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        entityManager.persist(user);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(user);
        entityManager.persist(item);

        comment = new Comment();
        comment.setText("Great drill!");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(Instant.parse("2022-03-25T14:00:00Z"));
        entityManager.persist(comment);
    }

    @Test
    void shouldFindAllByItemOrderByCreatedDesc() {
        Comment anotherComment = new Comment();
        anotherComment.setText("Needs improvement.");
        anotherComment.setItem(item);
        anotherComment.setAuthor(user);
        anotherComment.setCreated(Instant.parse("2022-03-26T14:00:00Z"));
        entityManager.persist(anotherComment);

        List<Comment> comments = commentRepository.findAllByItemOrderByCreatedDesc(item);

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getText()).isEqualTo("Needs improvement.");
        assertThat(comments.get(1).getText()).isEqualTo("Great drill!");
    }

    @Test
    void shouldReturnEmptyListIfNoComments() {
        Item anotherItem = new Item();
        anotherItem.setName("Screwdriver");
        anotherItem.setDescription("Small screwdriver");
        anotherItem.setAvailable(true);
        anotherItem.setOwner(user);
        entityManager.persist(anotherItem);

        List<Comment> comments = commentRepository.findAllByItemOrderByCreatedDesc(anotherItem);

        assertThat(comments).isEmpty();
    }
}
