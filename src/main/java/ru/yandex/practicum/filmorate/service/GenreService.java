package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {

    public static final String GENRE_NOT_FOUND = "Genre %s not found";
    private final GenreDao genreDao;

    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public List<Genre> findAll() {
        return genreDao.findAll().stream().sorted(Comparator.comparingLong(Genre::getId)).collect(Collectors.toList());
    }

    public Genre findById(Long id) {
        return genreDao.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(GENRE_NOT_FOUND, id)));
    }
}
