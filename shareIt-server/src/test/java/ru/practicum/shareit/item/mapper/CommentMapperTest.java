package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {
    private CommentMapper commentMapper;

    @BeforeEach
    void setup() {
        commentMapper = Mappers.getMapper(CommentMapper.class);
    }

    @Test
    void shouldMapCommentToCommentDTO() {
        User author = new User();
        author.setId(1);
        author.setName("John Doe");
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setAuthor(author);
        comment.setCreated(Instant.now());

        CommentDTO commentDTO = commentMapper.toDTO(comment);

        assertNotNull(commentDTO);
        assertEquals(comment.getText(), commentDTO.getText());
        assertEquals(author.getName(), commentDTO.getAuthorName());
    }

    @Test
    void shouldMapCommentDTOToComment() {
        CommentDTO commentDTO = CommentDTO.builder()
                .id(1)
                .text("Great item!")
                .authorName("John Doe")
                .build();

        Comment comment = commentMapper.toModel(commentDTO);

        assertNotNull(comment);
        assertEquals(commentDTO.getText(), comment.getText());
    }

    @Test
    void shouldMapListCommentToListCommentDTO() {
        User author = new User();
        author.setId(1);
        author.setName("John Doe");
        Comment comment1 = new Comment();
        comment1.setId(1);
        comment1.setText("Great item!");
        comment1.setAuthor(author);
        comment1.setCreated(Instant.now());

        Comment comment2 = new Comment();
        comment2.setId(2);
        comment2.setText("Needs improvement.");
        comment2.setAuthor(author);
        comment2.setCreated(Instant.now());

        List<Comment> comments = Arrays.asList(comment1, comment2);
        List<CommentDTO> commentDTOs = commentMapper.toListDTO(comments);

        assertNotNull(commentDTOs);
        assertEquals(2, commentDTOs.size());
        assertEquals(comments.get(0).getText(), commentDTOs.get(0).getText());
        assertEquals(comments.get(1).getText(), commentDTOs.get(1).getText());
    }

    @Test
    void shouldHandleNullCommentWhenMappingToDTO() {
        assertNull(commentMapper.toDTO(null), "Mapping of null comment should return null.");
    }

    @Test
    void shouldHandleNullCommentListWhenMappingToListDTO() {
        List<CommentDTO> result = commentMapper.toListDTO(null);
        assertNull(result, "Mapping of null comment list should return null.");
    }

    @Test
    void shouldHandleEmptyCommentListWhenMappingToListDTO() {
        List<CommentDTO> result = commentMapper.toListDTO(Collections.emptyList());
        assertNotNull(result);
        assertEquals(0, result.size(), "Mapping of empty comment list should return empty list.");
    }

    @Test
    void shouldHandleNullCommentDTOWhenMappingToModel() {
        assertNull(commentMapper.toModel(null), "Mapping of null CommentDTO should return null.");
    }

    @Test
    void shouldMapEmptyFieldsInCommentDTOToModel() {
        CommentDTO commentDTO = CommentDTO.builder().build();

        Comment result = commentMapper.toModel(commentDTO);

        assertNotNull(result);
        assertNull(result.getText(), "Text field should be null for empty CommentDTO.");
        assertNull(result.getAuthor(), "Author should be null for empty CommentDTO.");
    }

    @Test
    void shouldMapCreatedWhenConvertingToModel() {
        CommentDTO commentDTO = CommentDTO.builder()
                .id(1)
                .text("Great item!")
                .authorName("John Doe")
                .created("2020-10-10T10:00:00Z")
                .build();

        Comment result = commentMapper.toModel(commentDTO);

        assertNotNull(result);
        assertNotNull(result.getCreated());
        assertEquals(Instant.parse("2020-10-10T10:00:00Z"), result.getCreated());
    }

    @Test
    void toDTOShouldHandleNullAuthor() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setAuthor(null);

        CommentDTO result = commentMapper.toDTO(comment);

        assertNotNull(result);
        assertEquals("Great item!", result.getText());
        assertNull(result.getAuthorName(), "Author name should be null if author is null.");
    }

    @Test
    void toDTOShouldHandleNullAuthorName() {
        User author = new User();
        author.setName(null);
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setAuthor(author);

        CommentDTO result = commentMapper.toDTO(comment);

        assertNotNull(result);
        assertEquals("Great item!", result.getText());
        assertNull(result.getAuthorName(), "Author name should be null if author's name is null.");
    }

    @Test
    void toDTOShouldReturnAuthorName() {
        User author = new User();
        author.setId(1);
        author.setName("John Doe");
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setAuthor(author);

        CommentDTO result = commentMapper.toDTO(comment);

        assertNotNull(result);
        assertEquals("Great item!", result.getText());
        assertEquals("John Doe", result.getAuthorName(), "Author name should be 'John Doe'.");
    }

}

