package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService service;

    @Autowired
    ObjectMapper mapper;

    private ItemRequestDTO itemRequestDTO;

    @BeforeEach
    void setUp() {
        itemRequestDTO = ItemRequestDTO.builder()
                .id(1)
                .description("Need a drill")
                .build();
    }

    @Test
    void saveItemRequestShouldCreateRequest() throws Exception {
        when(service.saveItemRequest(eq(1), any(ItemRequestDTO.class))).thenReturn(itemRequestDTO);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDTO.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDTO.getDescription())));
    }

    @Test
    void findAllByRequestorShouldReturnListOfRequests() throws Exception {
        when(service.findAllByRequestor(eq(1))).thenReturn(Collections.singletonList(itemRequestDTO));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDTO.getId())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDTO.getDescription())));
    }

    @Test
    void findAllShouldReturnPaginatedRequests() throws Exception {
        when(service.findAll(eq(1), eq(0), eq(10))).thenReturn(Collections.singletonList(itemRequestDTO));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDTO.getId())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDTO.getDescription())));
    }

    @Test
    void findByIdShouldReturnRequest() throws Exception {
        when(service.findById(eq(1), eq(1))).thenReturn(itemRequestDTO);

        mvc.perform(get("/requests/{id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDTO.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDTO.getDescription())));
    }
}
