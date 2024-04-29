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
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BookinglValidationException;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingOutDTO bookingOutDTO;
    private BookingDTO bookingDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("John Doe");

        bookingDTO = BookingDTO.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingOutDTO = BookingOutDTO.builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void saveBookingShouldCreateBooking() throws Exception {
        when(bookingService.saveBooking(anyInt(), any())).thenReturn(bookingOutDTO);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingOutDTO.getId())))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDTO.getBooker().getId())))
                .andExpect(jsonPath("$.status", is(bookingOutDTO.getStatus().toString())));
    }

    @Test
    void updateStatusShouldModifyBookingStatus() throws Exception {
        when(bookingService.updateStatus(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingOutDTO);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDTO.getId())))
                .andExpect(jsonPath("$.status", is(bookingOutDTO.getStatus().toString())));
    }

    @Test
    void findByIdShouldReturnBooking() throws Exception {
        when(bookingService.findById(anyInt(), anyInt())).thenReturn(bookingOutDTO);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDTO.getId())))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDTO.getBooker().getId())))
                .andExpect(jsonPath("$.status", is(bookingOutDTO.getStatus().toString())));
    }

    @Test
    void findByBookerAndStateShouldReturnListOfBookings() throws Exception {
        when(bookingService.findByBookerAndState(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(Collections.singletonList(bookingOutDTO));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingOutDTO.getId())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingOutDTO.getBooker().getId())))
                .andExpect(jsonPath("$[0].status", is(bookingOutDTO.getStatus().toString())));
    }

    @Test
    void findByOwnerAndStateShouldReturnListOfBookings() throws Exception {
        when(bookingService.findByOwnerAndState(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(Collections.singletonList(bookingOutDTO));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingOutDTO.getId())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingOutDTO.getBooker().getId())))
                .andExpect(jsonPath("$[0].status", is(bookingOutDTO.getStatus().toString())));
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
