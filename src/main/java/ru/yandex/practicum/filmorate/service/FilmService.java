package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.dao.likes.LikesDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public class FilmService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final FilmDao filmDao;
    private final LikesDao likesDao;
    private final UserService userService;

    public FilmService(FilmDao filmDao, LikesDao likesDao, UserService userService) {
        this.filmDao = filmDao;
        this.likesDao = likesDao;
        this.userService = userService;
    }

    public List<Film> findAll() {
        return filmDao.findAll();
    }

    public Film findById(Long id) {
        return filmDao.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Film %s not found and cannot be updated", id)));
    }

    public Film create(Film film) {
        Film savedFilm = filmDao.createFilm(film);
        log.debug("{} has been added.", savedFilm);
        return savedFilm;
    }

    public Film update(Long id, Film film) {
        Film previous = findById(id);
        filmDao.updateFilm(id, film);
        log.debug("Film updated. Before: {}, after: {}", previous, film);
        return film;
    }

    public void addLike(Long id, Long userId) {
        if (userService.existById(userId)) {
            likesDao.addLike(userId, id);
            log.debug("User {} liked film {}", userId, id);
        } else {
            throw new NotFoundException(String.format(UserService.USER_NOT_FOUND, userId));
        }
    }

    public void removeLike(Long id, Long userId) {
        if (userService.existById(userId)) {
            likesDao.removeLike(userId, id);
            log.debug("User {} removed like from film {}", userId, id);
        } else {
            throw new NotFoundException(String.format(UserService.USER_NOT_FOUND, userId));
        }
    }

    public List<Film> getPopular(Integer count) {
        return likesDao.getPopular(count);
    }
}
