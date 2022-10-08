package ru.yandex.practicum.filmorate.dao.likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.director.DirectorDao;
import ru.yandex.practicum.filmorate.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.List;

@Repository
public class LikesDaoImpl implements LikesDao {

    private static final String DELETE_LIKE_SQL = "DELETE FROM film_likes WHERE user_id = ? AND film_id = ?";
    private static final String INSERT_LIKE_SQL = "INSERT INTO film_likes values ( ? , ? )";
    private static final String SELECT_POPULAR_SQL = "SELECT film.*, mpa.name as mpa_name, count(user_id) rate " +
            "FROM film LEFT JOIN film_likes fl ON film.id = fl.film_id " +
            "LEFT JOIN mpa ON mpa.id = mpa_id " +
            "GROUP BY film.id " +
            "ORDER BY rate DESC, film.id " +
            "LIMIT ?";
    private static final String SELECT_LIKE_EXIST = "SELECT count(*) FROM film_likes WHERE user_id = ? AND film_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> rowMapper;
    private final GenreDao genreDao;
    private final DirectorDao directorDao;

    @Autowired
    public LikesDaoImpl(JdbcTemplate jdbcTemplate, RowMapper<Film> filmMapper,
                        GenreDao genreDao, DirectorDao directorDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = filmMapper;
        this.genreDao = genreDao;
        this.directorDao = directorDao;
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        jdbcTemplate.update(INSERT_LIKE_SQL, userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        jdbcTemplate.update(DELETE_LIKE_SQL, userId, filmId);
    }

    @Override
    public List<Film> getPopular(int count) {
        List<Film> films = jdbcTemplate.query(SELECT_POPULAR_SQL, rowMapper, count);
        for (Film film : films) {
            film.setGenres(new HashSet<>(genreDao.findByFilmId(film.getId())));
            film.setDirectors(new HashSet<>(directorDao.findByFilmId(film.getId())));
        }
        return films;
    }

    @Override
    public boolean isLikeExist(Long userId, Long filmId) {
        return jdbcTemplate.queryForObject(SELECT_LIKE_EXIST, Integer.class, userId, filmId) > 0;
    }
}
