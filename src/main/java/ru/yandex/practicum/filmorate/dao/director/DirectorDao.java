package ru.yandex.practicum.filmorate.dao.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorDao {

    List<Director> findAll();

    Optional<Director> findById(Long id);

    List<Director> findByFilmId(Long id);

    List<Long> findFilmsByDirectorId(Long id, String sort);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    boolean existsById(Long id);

    void deleteById(Long id);
}
