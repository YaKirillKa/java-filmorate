package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            film.setId(++lastId);
        }
        films.put(film.getId(), film);
        return film;
    }
}
