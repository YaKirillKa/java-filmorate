package ru.yandex.practicum.filmorate.controller;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ConversionService conversionService;

    private final RecommendationService recommendationService;
    private final UserMapper userMapper;

    public UserController(UserService userService, ConversionService conversionService, UserMapper userMapper,
                          RecommendationService recommendationService) {
        this.userService = userService;
        this.conversionService = conversionService;
        this.recommendationService = recommendationService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll().stream()
                .map(user -> conversionService.convert(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return conversionService.convert(user, UserDto.class);
    }

    @GetMapping("{id}/friends")
    public List<UserDto> getFriends(@PathVariable Long id) {
        return userService.getFriends(id).stream()
                .map(user -> conversionService.convert(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId).stream()
                .map(user -> conversionService.convert(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDto create(@Valid @NotNull @RequestBody UserDto userDto) {
        User user = userMapper.mapToUser(userDto);
        user = userService.create(user);
        return conversionService.convert(user, UserDto.class);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDto update(@Valid @NotNull @RequestBody UserDto userDto) {
        User user = userMapper.mapToUser(userDto);
        user = userService.update(user.getId(), user);
        return conversionService.convert(user, UserDto.class);
    }

    @GetMapping("{id}/recommendations")
    public List<FilmDto> getRecommendation(@PathVariable Long id) {
        return recommendationService.getRecommendation(id).stream()
                .map(film -> conversionService.convert(film, FilmDto.class))
                .collect(Collectors.toList());
    }
}
