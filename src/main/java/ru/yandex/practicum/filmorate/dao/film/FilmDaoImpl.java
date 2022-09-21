package ru.yandex.practicum.filmorate.dao.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.dao.mpa.MpaDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FilmDaoImpl implements FilmDao {

    private static final String IS_EXIST_SQL = "SELECT EXISTS(SELECT * FROM film WHERE id = ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM film";
    private static final String SELECT_FILM_SQL = "SELECT f.id as id," +
            "f.name as name, " +
            "f.DESCRIPTION as description, " +
            "f.RELEASE_DATE as release_date, " +
            "f.DURATION as duration, " +
            "m.id as mpa_id, " +
            "m.NAME as mpa_name " +
            "FROM film f " +
            "LEFT JOIN mpa m ON m.id = f.mpa_id " +
            "WHERE f.id = ?";
    private static final String INSERT_FILM_SQL = "INSERT INTO film(name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_SQL = "UPDATE film SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE id = ?";
    private static final String DELETE_FILM_SQL = "DELETE FROM film WHERE id = ?";

    private static final String SELECT_GENRES_FILM_SQL = "SELECT genre_id FROM film_genre WHERE film_id = ?";
    private static final String INSERT_FILM_GENRES_SQL = "INSERT INTO film_genre VALUES (?,?)";
    private static final String DELETE_FILM_GENRES_SQL = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final RowMapper<Film> filmMapper;

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, MpaDao mpaDao, GenreDao genreDao, RowMapper<Film> filmMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
        this.filmMapper = filmMapper;
    }

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, filmMapper);
    }

    @Override
    public Optional<Film> findById(Long id) {
        Film film = null;
        Mpa mpa ;
        Set<Genre> genres;
        try {
            film = jdbcTemplate.queryForObject(SELECT_FILM_SQL, filmMapper, id);
            if (film == null) {
                return Optional.empty();
            }
            mpa = mpaDao.findById(film.getMpa().getId()).orElse(null);
            genres = new HashSet<>(genreDao.findByFilmId(id));
            film.setMpa(mpa);
            film.setGenres(genres);
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
            updateGenres(film, INSERT_FILM_GENRES_SQL,
                    film.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
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
            film.setGenres(new HashSet<>());
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
        updateGenres(film, DELETE_FILM_GENRES_SQL, genresToRemove);
        updateGenres(film, INSERT_FILM_GENRES_SQL, genresToInsert);

    }

    @Override
    public boolean existsById(Long id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_EXIST_SQL, Boolean.class, id));
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_FILM_SQL, id);
    }

    private void updateGenres(Film film, String query, List<Long> genresId) {
        if (genresId.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, genresId.get(i));
            }

            @Override
            public int getBatchSize() {
                return genresId.size();
            }
        });
    }
}
