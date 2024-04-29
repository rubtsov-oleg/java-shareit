package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.practicum.shareit.anotation.MarkerOfCreate;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(LocalValidatorFactoryBean.class)
public class ItemDTOTest {

    @Autowired
    private JacksonTester<ItemDTO> itemJson;

    @Autowired
    private JacksonTester<ItemOutDTO> itemOutJson;

    @Autowired
    private JacksonTester<ItemShortDTO> itemShortJson;

    @Autowired
    private Validator validator;

    @Test
    void testItemDtoSerialization() throws Exception {
        ItemDTO itemDTO = ItemDTO.builder()
                .id(1)
                .name("Drill")
                .description("A powerful drill")
                .available(true)
                .build();

        assertThat(itemJson.write(itemDTO)).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
    }

    @Test
    void testItemOutDtoSerialization() throws Exception {
        ItemOutDTO itemOutDTO = ItemOutDTO.builder()
                .id(1)
                .name("Drill")
                .description("A powerful drill")
                .available(true)
                .build();

        assertThat(itemOutJson.write(itemOutDTO)).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
    }

    @Test
    void testItemShortDtoSerialization() throws Exception {
        ItemShortDTO itemShortDTO = ItemShortDTO.builder()
                .id(1)
                .name("Drill")
                .description("Small but powerful")
                .available(true)
                .build();

        assertThat(itemShortJson.write(itemShortDTO)).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
    }

    @Test
    void testItemDtoValidation() {
        ItemDTO itemDTO = ItemDTO.builder()
                .name(" ")
                .description(" ")
                .available(null)
                .build();

        Set<ConstraintViolation<ItemDTO>> violations = validator.validate(itemDTO, MarkerOfCreate.class);
        assertThat(violations).isNotEmpty();
    }
}
