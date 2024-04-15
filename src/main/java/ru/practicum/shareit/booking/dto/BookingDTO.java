package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDTO {
    private Integer id;
    @NotNull(message = "start can`t null!")
    @Future(message = "start can`t be in past!")
    private LocalDateTime start;
    @NotNull(message = "end can`t null!")
    @Future(message = "end can`t be in past!")
    private LocalDateTime end;
    private Integer itemId;
    private Integer bookerId;
    private BookingStatus status;
}
