package ru.yandex.practicum.filmorate.dao.director;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class DirectorDaoImpl implements DirectorDao {

    private static final String SELECT_ALL_SQL = "SELECT * FROM director ORDER BY id";
    private static final String SELECT_DIRECTOR_SQL = "SELECT * FROM director WHERE id = ?";
    private static final String SELECT_DIRECTOR_BY_FILM_ID = "SELECT d.* FROM director d "
            + "JOIN film_director fd ON fd.director_id = d.id WHERE fd.film_id = ?";
    private static final String SELECT_FILMS_BY_DIRECTOR_ID_SORTED_LIKES = "SELECT film_id FROM "
            + "(SELECT fd.film_id, COUNT(user_id) AS likes "
            + "FROM FILM_DIRECTOR fd LEFT JOIN film_likes fl ON fd.film_id = fl.film_id "
            + "WHERE fd.DIRECTOR_ID = ? GROUP BY fd.film_id ORDER BY likes DESC)";
    private static final String SELECT_FILMS_BY_DIRECTOR_ID_SORTED_DATE = "SELECT film_id FROM (SELECT * "
            + "FROM film_director fd JOIN film f ON fd.film_id = f.id WHERE director_id = ? ORDER BY release_date)";
    private static final String INSERT_DIRECTOR_SQL = "INSERT INTO director(name) VALUES(?)";
    private static final String UPDATE_DIRECTOR_SQL = "UPDATE director SET name = ? WHERE id = ?";
    private static final String IS_EXISTS_SQL = "SELECT EXISTS(SELECT * FROM director WHERE id = ?)";
    private static final String DELETE_DIRECTOR_SQL = "DELETE FROM director WHERE id = ?";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    private final BeanPropertyRowMapper<Director> directorMapper = new BeanPropertyRowMapper<>(Director.class);

    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, directorMapper);
    }

    @Override
    public Optional<Director> findById(Long id) {
        Director director = null;
        try {
            director = jdbcTemplate.queryForObject(SELECT_DIRECTOR_SQL, directorMapper, id);
        } catch (DataAccessException e) {
            log.debug("Wrong ID: {}, message: {}", id, e.getMessage());
        }
        return Optional.ofNullable(director);
    }

    @Override
    public List<Director> findByFilmId(Long id) {
        return jdbcTemplate.query(SELECT_DIRECTOR_BY_FILM_ID, directorMapper, id);
    }

    @Override
    public List<Long> findFilmsIdByDirectorId(Long id, String sort) {
        if (sort.equals("year")) {
            return jdbcTemplate.queryForList(SELECT_FILMS_BY_DIRECTOR_ID_SORTED_DATE, Long.class, id);
        } else {
            return jdbcTemplate.queryForList(SELECT_FILMS_BY_DIRECTOR_ID_SORTED_LIKES, Long.class, id);
        }
    }

    @Override
    public Director createDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement(INSERT_DIRECTOR_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        jdbcTemplate.update(UPDATE_DIRECTOR_SQL, director.getName(), director.getId());
        return director;
    }

    @Override
    public boolean existsById(Long id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_EXISTS_SQL, Boolean.class, id));
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_DIRECTOR_SQL, id);
    }
}
