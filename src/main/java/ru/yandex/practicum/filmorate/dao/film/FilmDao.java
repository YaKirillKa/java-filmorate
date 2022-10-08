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
     * @param id   of the film to be updated.
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

    /**
     * Returns all movies that contain the desired substring in the title or the name of the director.
     *
     * @param titleQuery    substring for searching by names.
     * @param directorQuery substring for searching by directors.
     * @return all films satisfying the request.
     */
    List<Film> findFilms(String titleQuery, String directorQuery);

    /**
     * Returns {@link Film} that both users have liked.
     *
     * @param userId of a first user.
     * @param friendId of a other user
     * @return {@link List<Film>}
     */
    List<Film> findCommonFilmsByUsersId(Long userId, Long friendId);
}
