package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private Long lastId = 0L;

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(++lastId);
        updateUser(user);
        log.debug("{} has been added.", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        final Long id = user.getId();
        if (id == null) {
            log.debug("Got user with null ID: {}", user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID should not be null");
        }
        if (!users.containsKey(id)) {
            log.debug("User with ID: {} doesn't exists.", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID: '" + id + "' doesn't exists.");
        }
        User previous = updateUser(user);
        log.debug("User updated. Before: {}, after: {}", previous, user);
        return user;
    }

    private User updateUser(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return users.put(user.getId(), user);
    }
}
