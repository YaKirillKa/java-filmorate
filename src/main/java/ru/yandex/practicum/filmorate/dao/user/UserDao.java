package ru.yandex.practicum.filmorate.dao.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

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

    /**
     * Adds the user to the friends list.
     *
     * @param userId id of the {@link User} who adds.
     * @param friendId id of the {@link User} who should be added.
     */
    void addFriend(Long userId, Long friendId);

    /**
     * Removes the user from the friends list.
     *
     * @param userId id of the {@link User} who removes.
     * @param friendId id of the {@link User} who should be removed.
     */
    void removeFriend(Long userId, Long friendId);

    /**
     * Returns {@link List} of friends.
     *
     * @param id of the user which friends should be returned.
     * @return {@link List} of {@link User} or empty list.
     */
    List<User> getFriends(Long id);
}
