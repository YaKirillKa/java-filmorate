package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User %s not found", id)));
    }

    public User create(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        User savedUser = userStorage.createUser(user);
        log.debug("{} has been added.", savedUser);
        return savedUser;
    }

    public User update(Long id, User user) {
        User previous = findById(id);
        userStorage.updateUser(id, user);
        log.debug("User updated. Before: {}, after: {}", previous, user);
        return user;
    }

    public void addFriend(Long id, Long friendId) {
        User user = findById(id);
        User friend = findById(friendId);
        user.getFriendIds().add(friend.getId());
        friend.getFriendIds().add(user.getId());
        log.debug("User {} is friends with user {}", id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        User user = findById(id);
        User friend = findById(friendId);
        if (user.getFriendIds().remove(friend.getId()) && friend.getFriendIds().remove(user.getId())) {
            log.debug("User {} has stopped being friends with user {}", id, friendId);
            return;
        }
        throw new NotFoundException(String.format("User %s is not friend with %s", id, friendId));
    }

    public List<User> getFriends(Long id) {
        User user = findById(id);
        return user.getFriendIds().stream()
                .map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = findById(id);
        return user.getFriendIds().stream()
                .map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(o1 -> o1.getFriendIds().contains(otherId))
                .collect(Collectors.toList());
    }
}
