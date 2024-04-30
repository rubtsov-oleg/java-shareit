package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.OwnerValidationException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.item.dto.CommentDTO;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private ItemDTO itemDTO;
    private ItemOutDTO itemOutDTO;

    @BeforeEach
    void setUp() {
        itemDTO = ItemDTO.builder()
                .id(1)
                .name("Drill")
                .description("A powerful drill")
                .available(true)
                .build();

        itemOutDTO = ItemOutDTO.builder().id(1).name("Drill").description("A powerful drill").build();
    }

    @Test
    void saveItemShouldCreateItem() throws Exception {
        when(itemService.saveItem(eq(1), any(ItemDTO.class))).thenReturn(itemDTO);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDTO.getId())))
                .andExpect(jsonPath("$.name", is(itemDTO.getName())));
    }

    @Test
    void updateItemShouldModifyItem() throws Exception {
        when(itemService.updateItem(eq(1), eq(1), any(ItemDTO.class))).thenReturn(itemDTO);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDTO.getId())))
                .andExpect(jsonPath("$.name", is(itemDTO.getName())));
    }

    @Test
    void findByIdShouldReturnItem() throws Exception {
        when(itemService.findById(eq(1), eq(1))).thenReturn(itemOutDTO);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemOutDTO.getId())))
                .andExpect(jsonPath("$.name", is(itemOutDTO.getName())));
    }

    @Test
    void findAllByOwnerShouldReturnListOfItems() throws Exception {
        when(itemService.findAllByOwner(eq(1), eq(0), eq(10))).thenReturn(Collections.singletonList(itemOutDTO));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemOutDTO.getId())))
                .andExpect(jsonPath("$[0].name", is(itemOutDTO.getName())));
    }

    @Test
    void searchShouldReturnFilteredItems() throws Exception {
        when(itemService.search(any(String.class), eq(0), eq(10))).thenReturn(Collections.singletonList(itemOutDTO));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "drill")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemOutDTO.getId())))
                .andExpect(jsonPath("$[0].name", is(itemOutDTO.getName())));
    }

    @Test
    void createCommentShouldCreateComment() throws Exception {
        CommentDTO commentDTO = CommentDTO.builder().text("Great tool!").build();

        when(itemService.createComment(eq(1), eq(1), any(CommentDTO.class))).thenReturn(commentDTO);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDTO.getText())));
    }

    @Test
    void updateItemShouldReturnNotFound() throws Exception {
        when(itemService.updateItem(eq(1), eq(1), any(ItemDTO.class))).thenThrow(OwnerValidationException.class);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllByOwnerShouldReturnNotFound() throws Exception {
        when(itemService.findAllByOwner(eq(1), eq(0), eq(10))).thenThrow(NoSuchElementException.class);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }
}
