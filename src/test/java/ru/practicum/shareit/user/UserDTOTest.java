package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.practicum.shareit.anotation.MarkerOfCreate;
import ru.practicum.shareit.anotation.MarkerOfUpdate;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(LocalValidatorFactoryBean.class)
public class UserDTOTest {

    @Autowired
    private JacksonTester<UserDTO> json;

    @Autowired
    private Validator validator;

    @Test
    void testUserDtoSerialization() throws Exception {
        UserDTO userDTO = UserDTO.builder().id(1).email("test@mail.ru").name("test").build();

        JsonContent<UserDTO> result = json.write(userDTO);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@mail.ru");
    }

    @Test
    void testValidationOnCreate() {
        UserDTO userDTO = UserDTO.builder().id(1).name("test").email("test@mail.ru").build();

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO, MarkerOfCreate.class);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testValidationOnUpdate() {
        UserDTO userDTO = UserDTO.builder().name("test").email("test@mail.ru").build();

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO, MarkerOfUpdate.class);
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidEmailOnCreate() {
        UserDTO userDTO = UserDTO.builder().name("test").email("invalid_email").build();

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO, MarkerOfCreate.class);
        assertThat(violations).isNotEmpty();
    }

    @Test
    public void testGettersAndSetters() {
        UserDTO user = UserDTO.builder()
                .id(null)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
    }
}
