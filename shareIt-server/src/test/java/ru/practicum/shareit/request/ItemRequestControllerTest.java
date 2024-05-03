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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDTO itemRequestDTO;

    @BeforeEach
    void setUp() {
        itemRequestDTO = ItemRequestDTO.builder()
                .id(1)
                .description("Need a drill")
                .items(List.of())
                .build();
    }

    @Test
    void saveItemRequestShouldCreateRequest() throws Exception {
        when(itemRequestService.saveItemRequest(anyInt(), eq(itemRequestDTO))).thenReturn(itemRequestDTO);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDTO.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDTO.getDescription())));
    }

    @Test
    void findAllByRequestorShouldReturnRequestsList() throws Exception {
        when(itemRequestService.findAllByRequestor(anyInt())).thenReturn(Collections.singletonList(itemRequestDTO));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDTO.getId())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDTO.getDescription())));
    }

    @Test
    void findAllShouldReturnPagedRequestsList() throws Exception {
        when(itemRequestService.findAll(anyInt(), anyInt(), anyInt())).thenReturn(Collections.singletonList(itemRequestDTO));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDTO.getId())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDTO.getDescription())));
    }

    @Test
    void findByIdShouldReturnRequest() throws Exception {
        when(itemRequestService.findById(anyInt(), anyInt())).thenReturn(itemRequestDTO);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDTO.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDTO.getDescription())));
    }
}
