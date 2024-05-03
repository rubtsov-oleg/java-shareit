package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(LocalValidatorFactoryBean.class)
public class ItemRequestDTOTest {

    @Autowired
    private JacksonTester<ItemRequestDTO> json;

    @Autowired
    private Validator validator;

    @Test
    void testItemRequestDtoSerialization() throws Exception {
        ItemRequestDTO itemRequestDTO = ItemRequestDTO.builder()
                .id(1)
                .description("Need a drill")
                .created("2022-03-25T14:00:00")
                .items(Collections.emptyList())
                .build();

        JsonContent<ItemRequestDTO> result = json.write(itemRequestDTO);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-03-25T14:00:00");
    }

    @Test
    void testItemRequestDtoValidation() {
        ItemRequestDTO itemRequestDTO = ItemRequestDTO.builder()
                .description("")
                .build();

        Set<ConstraintViolation<ItemRequestDTO>> violations = validator.validate(itemRequestDTO);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("description can`t null!");
    }

    @Test
    public void testItemRequestDtoGettersAndSetters() {
        ItemRequestDTO itemRequest = ItemRequestDTO.builder()
                .id(1)
                .description("Need a bike")
                .created("2022-03-26T15:00:00")
                .items(Collections.emptyList())
                .build();

        assertThat(itemRequest.getId()).isEqualTo(1);
        assertThat(itemRequest.getDescription()).isEqualTo("Need a bike");
        assertThat(itemRequest.getCreated()).isEqualTo("2022-03-26T15:00:00");
        assertThat(itemRequest.getItems()).isEmpty();
    }
}
