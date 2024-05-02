package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(LocalValidatorFactoryBean.class)
public class CommentDTOTest {
    @Autowired
    private JacksonTester<CommentDTO> commentJson;

    @Autowired
    private Validator validator;

    @Test
    void testCommentDtoSerialization() throws Exception {
        CommentDTO commentDTO = CommentDTO.builder()
                .id(1)
                .text("Great tool!")
                .build();

        assertThat(commentJson.write(commentDTO)).extractingJsonPathStringValue("$.text").isEqualTo("Great tool!");
    }

    @Test
    void testCommentDtoValidation() {
        CommentDTO invalidComment = CommentDTO.builder()
                .text(" ")
                .build();

        Set<ConstraintViolation<CommentDTO>> violations = validator.validate(invalidComment);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("text can`t null!");
    }
}
