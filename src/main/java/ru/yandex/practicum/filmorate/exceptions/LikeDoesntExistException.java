package ru.yandex.practicum.filmorate.exceptions;

public class LikeDoesntExistException extends RuntimeException {

    public LikeDoesntExistException(String message) {
        super(message);
    }
}
