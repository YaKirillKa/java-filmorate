package ru.yandex.practicum.filmorate.dao.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.director.DirectorDao;
import ru.yandex.practicum.filmorate.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FilmDaoImpl implements FilmDao {

    private static final String IS_EXIST_SQL = "SELECT EXISTS(SELECT * FROM film WHERE id = ?)";
    private static final String SELECT_ALL_SQL = "SELECT f.*, m.NAME as mpa_name " +
            "FROM film f LEFT JOIN mpa m ON m.id = f.mpa_id";
    private static final String SELECT_COLLECTION_SQL = SELECT_ALL_SQL + " WHERE f.id IN (:ids)";
    private static final String SELECT_FILM_SQL = "SELECT f.*, m.NAME as mpa_name " +
            "FROM film f LEFT JOIN mpa m ON m.id = f.mpa_id WHERE f.id = ?";
    private static final String INSERT_FILM_SQL = "INSERT INTO film(name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_SQL = "UPDATE film SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE id = ?";
    private static final String DELETE_FILM_SQL = "DELETE FROM film WHERE id = ?";

    private static final String SELECT_GENRES_FILM_SQL = "SELECT genre_id FROM film_genre WHERE film_id = ?";
    private static final String INSERT_FILM_GENRES_SQL = "INSERT INTO film_genre VALUES (?,?)";
    private static final String DELETE_FILM_GENRES_SQL = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
    private static final String SELECT_LIKES_INTERSECTION_SQL = "SELECT f.*, m.name mpa_name, COUNT(user_id) likes " +
            "FROM film f LEFT JOIN mpa m ON m.id = f.mpa_id LEFT JOIN film_likes fl ON fl.film_id = f.id " +
            "WHERE f.id IN (SELECT film_id FROM film_likes fl WHERE fl.user_id = ?) " +
            "AND f.id IN (SELECT film_id FROM film_likes fl WHERE fl.user_id = ?) " +
            "GROUP BY f.id ORDER BY likes DESC";

    private static final String SELECT_FILM_DIRECTORS_SQL = "SELECT director_id FROM film_director WHERE film_id = ?";
    private static final String INSERT_FILM_DIRECTORS_SQL = "INSERT INTO film_director VALUES(?,?)";
    private static final String DELETE_FILM_DIRECTORS_SQL = "DELETE FROM film_director WHERE film_id = ? AND director_id = ?";
    private static final String SELECT_FILMS_BY_SUBSTRING_SQL = "SELECT f.*, m.NAME as mpa_name " +
            "FROM film f LEFT JOIN mpa m ON m.id = f.mpa_id LEFT JOIN film_director fd ON fd.film_id=f.id " +
            "LEFT JOIN director d ON d.id = fd.director_id WHERE LOWER(d.NAME) LIKE ? OR LOWER(f.NAME) LIKE ? " +
            "ORDER BY f.id DESC ";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final GenreDao genreDao;
    private final DirectorDao directorDao;
    private final RowMapper<Film> filmMapper;

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                       GenreDao genreDao, DirectorDao directorDao, RowMapper<Film> filmMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.genreDao = genreDao;
        this.directorDao = directorDao;
        this.filmMapper = filmMapper;
    }

    @Override
    @Transactional
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(SELECT_ALL_SQL, filmMapper);
        for (Film film : films) {
            film.setGenres(new HashSet<>(genreDao.findByFilmId(film.getId())));
            film.setDirectors(new HashSet<>(directorDao.findByFilmId(film.getId())));
        }
        return films;
    }

    @Override
    @Transactional
    public List<Film> toFilm(List<Long> ids) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        List<Film> films = namedParameterJdbcTemplate.query(SELECT_COLLECTION_SQL,
                parameters, filmMapper);
        for (Film film : films) {
            Set<Genre> genres = new HashSet<>(genreDao.findByFilmId(film.getId()));
            film.setGenres(genres);
        }
        return films;
    }

    @Override
    @Transactional
    public Optional<Film> findById(Long id) {
        Film film = null;
        try {
            film = jdbcTemplate.queryForObject(SELECT_FILM_SQL, filmMapper, id);
            if (film == null) {
                return Optional.empty();
            }
            film.setGenres(new HashSet<>(genreDao.findByFilmId(id)));
            film.setDirectors(new HashSet<>(directorDao.findByFilmId(id)));
        } catch (DataAccessException e) {
            log.debug("Wrong ID: {}, message: {}", id, e.getMessage());
        }
        return Optional.ofNullable(film);
    }

    @Override
    @Transactional
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement(INSERT_FILM_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        final long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(id);
        if (film.getGenres() != null) {
            updateFilmData(film, INSERT_FILM_GENRES_SQL,
                    film.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
        }
        if (film.getDirectors() != null) {
            updateFilmData(film, INSERT_FILM_DIRECTORS_SQL,
                    film.getDirectors().stream().map(Director::getId).collect(Collectors.toList()));
        }
        return film;
    }

    @Override
    @Transactional
    public void updateFilm(Long id, Film film) {
        jdbcTemplate.update(UPDATE_FILM_SQL,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                id);
        if (film.getGenres() == null) {
            film.setGenres(Collections.emptySet());
        }
        List<Long> currentGenres = jdbcTemplate.query(SELECT_GENRES_FILM_SQL,
                ((rs, rowNum) -> rs.getLong("genre_id")), id);
        List<Long> newGenres = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        List<Long> genresToRemove = currentGenres.stream()
                .filter(i -> !newGenres.contains(i))
                .collect(Collectors.toList());
        List<Long> genresToInsert = newGenres.stream()
                .filter(i -> !currentGenres.contains(i))
                .collect(Collectors.toList());
        updateFilmData(film, DELETE_FILM_GENRES_SQL, genresToRemove);
        updateFilmData(film, INSERT_FILM_GENRES_SQL, genresToInsert);

        if (film.getDirectors() == null) {
            film.setDirectors(Collections.emptySet());
        }
        List<Long> currentDirectors = jdbcTemplate.query(SELECT_FILM_DIRECTORS_SQL,
                ((rs, rowNum) -> rs.getLong("director_id")), id);
        List<Long> newDirectors = film.getDirectors().stream().map(Director::getId).collect(Collectors.toList());
        List<Long> directorsToRemove = currentDirectors.stream()
                .filter(i -> !newDirectors.contains(i))
                .collect(Collectors.toList());
        List<Long> directorsToInsert = newDirectors.stream()
                .filter(i -> !currentGenres.contains(i))
                .collect(Collectors.toList());
        updateFilmData(film, DELETE_FILM_DIRECTORS_SQL, directorsToRemove);
        updateFilmData(film, INSERT_FILM_DIRECTORS_SQL, directorsToInsert);

    }

    @Override
    public boolean existsById(Long id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_EXIST_SQL, Boolean.class, id));
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_FILM_SQL, id);
    }

    @Override
    public List<Film> findCommonFilmsByUsersId(Long userId, Long friendId) {
        List<Film> films = jdbcTemplate.query(SELECT_LIKES_INTERSECTION_SQL, filmMapper, userId, friendId);
        for (Film film : films) {
            Set<Genre> genres = new HashSet<>(genreDao.findByFilmId(film.getId()));
            film.setGenres(genres);
        }
        return films;
    }

    @Override
    public List<Film> findFilms(String titleQuery, String directorQuery) {
        List<Film> films = jdbcTemplate.query(SELECT_FILMS_BY_SUBSTRING_SQL, filmMapper, directorQuery, titleQuery);
        for (Film film : films) {
            film.setGenres(new HashSet<>(genreDao.findByFilmId(film.getId())));
            film.setDirectors(new HashSet<>(directorDao.findByFilmId(film.getId())));
        }
        return films;
    }

    private void updateFilmData(Film film, String query, List<Long> data) {
        if (data.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, data.get(i));
            }

            @Override
            public int getBatchSize() {
                return data.size();
            }
        });
    }
}
