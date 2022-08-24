package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    /**
     * Returns all users.
     *
     * @return {@link List} of all users or empty {@link List}.
     */
    List<User> findAll();

    /**
     * Returns {@link User} by the given id.
     *
     * @param id of the user to be returned.
     * @return {@link User} wrapped in {@link Optional} or empty {@link Optional}.
     */
    Optional<User> findById(Long id);

    /**
     * Sets the next available ID and saves the {@link User} in storage.
     *
     * @param user the user to be saved.
     * @return saved {@link User}.
     */
    User createUser(User user);

    /**
     * Saves the given {@link User} by the given id.
     *
     * @param id of the user to be updated.
     * @param user the user to be saved.
     */
    void updateUser(Long id, User user);

    /**
     * Checks whether there is a {@link User} with the given id.
     *
     * @param id of the user to be checked.
     * @return true if the user exists or false.
     */
    boolean existsById(Long id);

    /**
     * Deletes the {@link User} by the given id.
     *
     * @param id of the user to be removed.
     */
    void deleteById(Long id);
}
