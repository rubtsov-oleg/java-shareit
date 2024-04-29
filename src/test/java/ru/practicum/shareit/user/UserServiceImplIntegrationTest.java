package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void shouldFindUserById() {
        UserDTO foundUser = userService.findById(user.getId());
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void shouldSaveNewUser() {
        UserDTO newUserDTO = UserDTO.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();
        UserDTO savedUser = userService.saveUser(newUserDTO);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Jane Doe");
    }

    @Test
    void shouldDeleteUser() {
        userService.delete(user.getId());
        assertThrows(NoSuchElementException.class, () -> userService.findById(user.getId()));
    }

    @Test
    void shouldUpdateUser() {
        UserDTO updatedUserDTO = UserDTO.builder()
                .id(user.getId())
                .email("new.email@example.com")
                .name("New Name")
                .build();
        UserDTO updatedUser = userService.updateUser(user.getId(), updatedUserDTO);
        assertThat(updatedUser.getEmail()).isEqualTo("new.email@example.com");
        assertThat(updatedUser.getName()).isEqualTo("New Name");
    }
}
