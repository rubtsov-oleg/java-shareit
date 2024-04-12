package ru.practicum.shareit.exceptions;

public class OwnerValidationException extends RuntimeException {
    public OwnerValidationException(String message) {
        super(message);
    }
}
