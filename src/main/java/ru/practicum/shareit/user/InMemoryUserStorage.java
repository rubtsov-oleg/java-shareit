package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailValidationException;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserDAO {
    private Integer id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        if (!checkExistEmail(user)) {
            throw new EmailValidationException(user.getEmail() + " данная почта уже используется");
        }
        user.setId(createIdUser());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        if (!checkExistEmail(user)) {
            throw new EmailValidationException(user.getEmail() + " данная почта уже используется");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean delete(Integer id) {
        User user = users.remove(id);
        return user != null;
    }

    @Override
    public boolean isExistById(Integer id) {
        return users.containsKey(id);
    }

    private Integer createIdUser() {
        return id++;
    }

    private Boolean checkExistEmail(User user) {
        for (User existedUser : users.values()) {
            if (existedUser.getEmail().equals(user.getEmail()) && (!existedUser.getId().equals(user.getId()))) {
                return false;
            }
        }
        return true;
    }
}