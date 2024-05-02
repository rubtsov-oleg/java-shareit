package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping("/{id}")
    public UserDTO findById(@PathVariable Integer id) {
        log.info("Получен запрос GET, на получения пользователя, по id: {}", id);
        return service.findById(id);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        log.info("Получен запрос GET, на получения всех пользователей.");
        List<UserDTO> userDTOList = service.getAllUsers();
        log.info("Получен ответ, список пользователей, размер: {}", userDTOList.size());
        return userDTOList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO saveUser(@RequestBody UserDTO userDTO) {
        log.info("Получен запрос Post, на обновление данных пользователя.");
        log.info("Добавлен пользователь: {}", userDTO.getName());
        return service.saveUser(userDTO);
    }

    @PatchMapping("/{id}")
    public UserDTO updateUser(@RequestBody UserDTO userDTO, @PathVariable Integer id) {
        log.info("Получен запрос Patch, на обновление пользователя");
        UserDTO userDTO1 = service.updateUser(id, userDTO);
        log.info("Обновлён пользователь: {}", userDTO1.getName());
        return userDTO1;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        log.info("Получен запрос DELETE, на удаления пользователя, по id: {}", id);
        service.delete(id);
    }
}