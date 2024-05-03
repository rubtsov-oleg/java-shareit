package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.anotation.MarkerOfCreate;
import ru.practicum.shareit.anotation.MarkerOfUpdate;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Integer id) {
        log.info("Получен запрос GET, на получения пользователя, по id: {}", id);
        return userClient.findById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получен запрос GET, на получения всех пользователей.");
        return userClient.getAllUsers();
    }

    @PostMapping
    @Validated({MarkerOfCreate.class})
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("Получен запрос Post, на создание нового пользователя: {}", userDTO.getName());
        return userClient.saveUser(userDTO);
    }

    @PatchMapping("/{id}")
    @Validated({MarkerOfUpdate.class})
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDTO userDTO, @PathVariable Integer id) {
        log.info("Получен запрос Patch, на обновление пользователя: {}", userDTO.getName());
        return userClient.updateUser(id, userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        log.info("Получен запрос DELETE, на удаления пользователя, по id: {}", id);
        return userClient.delete(id);
    }
}