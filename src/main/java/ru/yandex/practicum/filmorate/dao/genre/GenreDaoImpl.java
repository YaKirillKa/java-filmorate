package ru.yandex.practicum.filmorate.dao.genre;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDaoImpl implements GenreDao {
    private static final String SELECT_ALL_SQL = "SELECT * FROM genre";
    public static final String SELECT_GENRE_SQL = "SELECT * FROM genre WHERE id = ?";
    public static final String SELECT_GENRE_BY_FILM_SQL = "SELECT * FROM genre " +
            "WHERE id in (SELECT genre_id FROM film_genre WHERE film_id = ?)";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final BeanPropertyRowMapper<Genre> genreMapper = new BeanPropertyRowMapper<>(Genre.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, genreMapper);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        Genre genre = null;
        try {
            genre = jdbcTemplate.queryForObject(SELECT_GENRE_SQL, genreMapper, id);
        } catch (DataAccessException e) {
            log.debug("Wrong ID: {}, message: {}", id, e.getMessage());
        }
        return Optional.ofNullable(genre);
    }

    @Override
    @Transactional
    public List<Genre> findByFilmId(Long id) {
        return jdbcTemplate.query(SELECT_GENRE_BY_FILM_SQL, genreMapper, id);
    }
}
