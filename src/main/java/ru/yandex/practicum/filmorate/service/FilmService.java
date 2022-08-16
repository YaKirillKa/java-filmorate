package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        Optional<Film> optionalFilm = filmStorage.findById(id);
        if (optionalFilm.isEmpty()) {
            throw new NotFoundException(String.format("Film %s not found and cannot be updated", id));
        }
        return optionalFilm.get();
    }

    public Film create(Film film) {
        Film savedFilm = filmStorage.createFilm(film);
        log.debug("{} has been added.", savedFilm);
        return savedFilm;
    }

    public Film update(Long id, Film film) {
        Film previous = findById(id);
        filmStorage.updateFilm(id, film);
        log.debug("Film updated. Before: {}, after: {}", previous, film);
        return film;
    }

    public void addLike(Long id, Long userId) {
        Film film = findById(id);
        User user = userService.findById(userId);
        film.getLikes().add(user.getId());
        log.debug("User {} liked film {}", userId, id);
    }

    public void removeLike(Long id, Long userId) {
        Film film = findById(id);
        User user = userService.findById(userId);
        if (film.getLikes().remove(user.getId())) {
            log.debug("User {} removed like from film {}", userId, id);
            return;
        }
        throw new NotFoundException(String.format("User %s did not like film %s", userId, id));
    }

    public List<Film> getPopular(Integer count) {
        return findAll().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
