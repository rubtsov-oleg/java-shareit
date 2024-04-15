package ru.practicum.shareit.booking.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class BookerMapper {
    private final UserRepository userRepository;

    public User fromId(Integer id) {
        return id != null ? userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь " + id + " не найден")) : null;
    }
}
