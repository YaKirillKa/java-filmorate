package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.director.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
public class DirectorService {
    private static final String DIRECTOR_NOT_FOUND = "Director with ID = %d not found";
    private static final String DIRECTOR_ALREADY_EXISTS = "Director with ID = %d already exists";
    private final DirectorDao directorDao;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }


    public List<Director> findAll() {
        return directorDao.findAll();
    }

    public Director findById(Long id) {
        return directorDao.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(DIRECTOR_NOT_FOUND, id)));
    }

    public Director create(Director director) {
        if (directorDao.existsById(director.getId())) {
            throw new AlreadyExistsException(String.format(DIRECTOR_ALREADY_EXISTS, director.getId()));
        }
        log.debug("Director with ID = {} and name = {} has been added.", director.getId(), director.getName());
        return directorDao.createDirector(director);
    }

    public Director update(Director director) {
        if (!directorDao.existsById(director.getId())) {
            throw new NotFoundException(String.format(DIRECTOR_NOT_FOUND, director.getId()));
        }
        log.debug("Director updated. New name = {}.", director.getName());
        return directorDao.updateDirector(director);
    }

    public void deleteById(Long id) {
        if (!directorDao.existsById(id)) {
            throw new NotFoundException(String.format(DIRECTOR_NOT_FOUND, id));
        }
        directorDao.deleteById(id);
        log.debug("Director with ID = {} deleted.", id);
    }


}
