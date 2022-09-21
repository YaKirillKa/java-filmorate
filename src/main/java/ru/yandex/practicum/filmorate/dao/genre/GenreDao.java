package ru.yandex.practicum.filmorate.dao.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {

    List<Genre> findAll();

    Optional<Genre> findById(Long id);

    List<Genre> findByFilmId(Long id);

}
