package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDTO userOutDTO;
    private UserDTO userInDTO;

    @BeforeEach
    void setUp() {
        userInDTO = UserDTO.builder()
                .email("test@mail.ru")
                .name("test")
                .build();

        userOutDTO = UserDTO.builder()
                .id(1)
                .email("test@mail.ru")
                .name("test")
                .build();
    }

    @Test
    void findByIdShouldReturnUser() throws Exception {
        when(userService.findById(eq(1))).thenReturn(userOutDTO);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userOutDTO.getId())))
                .andExpect(jsonPath("$.name", is(userOutDTO.getName())))
                .andExpect(jsonPath("$.email", is(userOutDTO.getEmail())));
    }

    @Test
    void getAllUsersShouldReturnListOfUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userOutDTO));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userOutDTO.getId())))
                .andExpect(jsonPath("$[0].name", is(userOutDTO.getName())))
                .andExpect(jsonPath("$[0].email", is(userOutDTO.getEmail())));
    }

    @Test
    void saveUserShouldCreateUser() throws Exception {
        when(userService.saveUser(any(UserDTO.class))).thenReturn(userOutDTO);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userInDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userOutDTO.getId())))
                .andExpect(jsonPath("$.name", is(userOutDTO.getName())))
                .andExpect(jsonPath("$.email", is(userOutDTO.getEmail())));
    }

    @Test
    void updateUserShouldModifyUser() throws Exception {
        when(userService.updateUser(eq(1), any(UserDTO.class))).thenReturn(userOutDTO);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userInDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userOutDTO.getId())))
                .andExpect(jsonPath("$.name", is(userOutDTO.getName())))
                .andExpect(jsonPath("$.email", is(userOutDTO.getEmail())));
    }

    @Test
    void deleteUserShouldReturnNoContent() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenCreateUserWithInvalidEmail_thenReturns400() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"invalid-email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateUserWithoutName_thenReturns400() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@example.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateUserWithId_thenReturns400() throws Exception {
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"John\",\"email\":\"john@example.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateUserWithStringId_thenReturns500() throws Exception {
        mvc.perform(patch("/users/qwe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"John\"}"))
                .andExpect(status().isInternalServerError());
    }
}