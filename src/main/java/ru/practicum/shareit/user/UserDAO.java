package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User save(User user);

    Optional<User> findById(Integer id);

    List<User> findAll();

    User update(User user);

    boolean delete(Integer id);

    boolean isExistById(Integer id);
}
