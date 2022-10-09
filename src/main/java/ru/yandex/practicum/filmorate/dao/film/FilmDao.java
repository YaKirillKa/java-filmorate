package ru.yandex.practicum.filmorate.dao.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmDao {

    /**
     * Returns all films.
     *
     * @return {@link List} of all films or empty {@link List}.
     */
    List<Film> findAll();

    List<Film> toFilm(List<Long> film);

    /**
     * Returns {@link Film} by the given id.
     *
     * @param id of the film to be returned.
     * @return {@link Film} wrapped in {@link Optional} or empty {@link Optional}.
     */
    Optional<Film> findById(Long id);

    /**
     * Sets the next available ID and saves the {@link Film} in storage.
     *
     * @param film the film to be saved.
     * @return saved {@link Film}.
     */
    Film createFilm(Film film);

    /**
     * Saves the given {@link Film} by the given id.
     *
     * @param id of the film to be updated.
     * @param film the film to be saved.
     */
    void updateFilm(Long id, Film film);

    /**
     * Checks whether there is a {@link Film} with the given id.
     *
     * @param id of the film to be checked.
     * @return true if the film exists or false.
     */
    boolean existsById(Long id);

    /**
     * Deletes the {@link Film} by the given id.
     *
     * @param id of the film to be removed.
     */
    void deleteById(Long id);

}
