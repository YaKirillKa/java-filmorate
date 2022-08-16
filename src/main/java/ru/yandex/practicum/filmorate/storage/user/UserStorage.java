package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAll();

    Optional<User> findById(Long id);

    User createUser(User film);

    void updateUser(Long id, User film);

    boolean existsById(Long id);

    void deleteById(Long id);
}
