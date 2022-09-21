package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.user.UserDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    public static final String USER_NOT_FOUND = "User %s not found";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User findById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, id)));
    }

    public User create(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        User savedUser = userDao.createUser(user);
        log.debug("{} has been added.", savedUser);
        return savedUser;
    }

    public User update(Long id, User user) {
        if (!userDao.existsById(id)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, id));
        }
        User previous = findById(id);
        userDao.updateUser(id, user);
        log.debug("User updated. Before: {}, after: {}", previous, user);
        return user;
    }

    public void addFriend(Long id, Long friendId) {
        if (!userDao.existsById(id)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, id));
        }
        if (!userDao.existsById(friendId)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, friendId));
        }
        userDao.addFriend(id, friendId);
        log.debug("User {} is friends with user {}", id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        if (!userDao.existsById(id)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, id));
        }
        if (!userDao.existsById(friendId)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, friendId));
        }
        userDao.removeFriend(id, friendId);
    }

    public List<User> getFriends(Long id) {
        if (userDao.existsById(id)) {
            return userDao.getFriends(id);
        }
        throw new NotFoundException(String.format(USER_NOT_FOUND, id));
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        if (!userDao.existsById(id)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, id));
        }
        if (!userDao.existsById(otherId)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, otherId));
        }
        List<User> userList = userDao.getFriends(id);
        List<User> otherUserList = userDao.getFriends(otherId);

        return userList.stream()
                .distinct()
                .filter(otherUserList::contains)
                .collect(Collectors.toList());
    }

    public boolean existById(Long id) {
        return userDao.existsById(id);
    }
}
