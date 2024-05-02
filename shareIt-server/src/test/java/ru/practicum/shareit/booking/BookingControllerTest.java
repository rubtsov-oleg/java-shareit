package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;
import ru.practicum.shareit.exceptions.BookinglValidationException;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDTO bookingDTO;
    private BookingOutDTO bookingOutDTO;

    @BeforeEach
    void setUp() {
        bookingDTO = BookingDTO.builder()
                .id(1)
                .start(LocalDateTime.parse("2024-05-01T12:00:00"))
                .end(LocalDateTime.parse("2024-05-02T12:00:00"))
                .build();

        bookingOutDTO = BookingOutDTO.builder()
                .id(1)
                .start(LocalDateTime.parse("2024-05-01T12:00:00"))
                .end(LocalDateTime.parse("2024-05-02T12:00:00"))
                .build();
    }

    @Test
    void saveBookingShouldCreateBooking() throws Exception {
        when(bookingService.saveBooking(anyInt(), any(BookingDTO.class))).thenReturn(bookingOutDTO);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingOutDTO.getId())))
                .andExpect(jsonPath("$.status", is(bookingOutDTO.getStatus())));
    }

    @Test
    void updateStatusShouldChangeBookingStatus() throws Exception {
        when(bookingService.updateStatus(anyInt(), eq(1), eq(true))).thenReturn(bookingOutDTO);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDTO.getId())))
                .andExpect(jsonPath("$.status", is(bookingOutDTO.getStatus())));
    }

    @Test
    void findByIdShouldReturnBooking() throws Exception {
        when(bookingService.findById(anyInt(), eq(1))).thenReturn(bookingOutDTO);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDTO.getId())));
    }

    @Test
    void findByBookerAndStateShouldReturnBookingsList() throws Exception {
        when(bookingService.findByBookerAndState(anyInt(), eq("ALL"), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingOutDTO));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingOutDTO.getId())));
    }

    @Test
    void findByOwnerAndStateShouldReturnBookingsList() throws Exception {
        when(bookingService.findByOwnerAndState(anyInt(), eq("ALL"), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingOutDTO));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingOutDTO.getId())));
    }

    @Test
    void findByBookerAndStateShouldReturn400() throws Exception {
        when(bookingService.findByBookerAndState(anyInt(), anyString(), anyInt(), anyInt())).thenThrow(BookinglValidationException.class);
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "tm[")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
