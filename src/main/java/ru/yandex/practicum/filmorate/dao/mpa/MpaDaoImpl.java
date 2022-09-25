package ru.yandex.practicum.filmorate.dao.mpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDaoImpl implements MpaDao {

    private static final String SELECT_ALL_SQL = "SELECT * FROM mpa ORDER BY id";
    public static final String SELECT_MPA_SQL = "SELECT * FROM mpa WHERE id = ?";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final BeanPropertyRowMapper<Mpa> mpaMapper = new BeanPropertyRowMapper<>(Mpa.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, mpaMapper);
    }

    @Override
    @Transactional
    public Optional<Mpa> findById(Long id) {
        Mpa mpa = null;
        try {
            mpa = jdbcTemplate.queryForObject(SELECT_MPA_SQL, mpaMapper, id);
        } catch (DataAccessException e) {
            log.debug("Wrong ID: {}, message: {}", id, e.getMessage());
        }
        return Optional.ofNullable(mpa);
    }
}
