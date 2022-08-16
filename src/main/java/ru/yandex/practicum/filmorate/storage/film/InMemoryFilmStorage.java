package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long lastId = 0L;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(++lastId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void updateFilm(Long id, Film film) {
        films.put(id, film);
    }

    @Override
    public boolean existsById(Long id) {
        return films.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        films.remove(id);
    }
}
