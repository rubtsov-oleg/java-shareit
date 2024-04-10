package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class Booking {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer item;
    private Integer booker;
    private BookingStatus status;
}
