package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        userRepository.save(user);
    }

    @Test
    void whenFindById_thenReturnUser() {
        Optional<User> found = userRepository.findById(user.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void whenSaveUserWithDuplicateEmail_thenThrowException() {
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("john.doe@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(newUser));
    }

    @Test
    void whenDeleteById_thenUserShouldBeDeleted() {
        userRepository.deleteById(user.getId());
        Optional<User> deleted = userRepository.findById(user.getId());
        assertThat(deleted).isNotPresent();
    }

    @Test
    void whenFindAll_thenReturnUserList() {
        User anotherUser = new User();
        anotherUser.setName("Jane Roe");
        anotherUser.setEmail("jane.roe@example.com");
        userRepository.save(anotherUser);

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(2).extracting(User::getEmail)
                .contains(user.getEmail(), anotherUser.getEmail());
    }
}
