package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private Long lastId = 0L;

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(++lastId);
        films.put(film.getId(), film);
        log.debug("{} has been added.", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        final Long id = film.getId();
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID should not be null");
        }
        if (!films.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film with ID: '" + id + "' doesn't exists.");
        }
        Film previous = films.put(id, film);
        log.debug("Film updated. Before: {}, after: {}", previous, film);
        return film;
    }
}
