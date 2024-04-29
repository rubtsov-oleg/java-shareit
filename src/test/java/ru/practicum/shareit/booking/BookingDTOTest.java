package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(LocalValidatorFactoryBean.class)
public class BookingDTOTest {

    @Autowired
    private JacksonTester<BookingDTO> json;

    @Autowired
    private Validator validator;

    @Test
    void testBookingDtoSerialization() throws Exception {
        BookingDTO bookingDTO = BookingDTO.builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1)
                .bookerId(2)
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<BookingDTO> result = json.write(bookingDTO);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void testValidationSuccess() {
        BookingDTO bookingDTO = BookingDTO.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingDTO>> violations = validator.validate(bookingDTO);
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidDates() {
        BookingDTO bookingDTO = BookingDTO.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        Set<ConstraintViolation<BookingDTO>> violations = validator.validate(bookingDTO);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testNullFields() {
        BookingDTO bookingDTO = BookingDTO.builder().build();

        Set<ConstraintViolation<BookingDTO>> violations = validator.validate(bookingDTO);
        assertThat(violations).isNotEmpty();
    }
}
