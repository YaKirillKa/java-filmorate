package ru.yandex.practicum.filmorate.dao.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {
    private static final String IS_EXIST_SQL = "SELECT EXISTS(SELECT * FROM app_user WHERE id = ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM app_user";
    private static final String SELECT_USER_SQL = "SELECT * FROM app_user WHERE id = ?";
    private static final String INSERT_USER_SQL = "INSERT INTO app_user(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE app_user SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM app_user WHERE id = ?";
    private static final String SELECT_FRIENDS_SQL = "SELECT * FROM app_user WHERE id IN " +
            "(SELECT friend_id FROM user_friend WHERE user_id = ?)";
    private static final String INSERT_FRIEND_SQL = "INSERT INTO user_friend VALUES(?, ?)";
    private static final String DELETE_FRIEND_SQL = "DELETE FROM user_friend WHERE user_id = ? AND friend_id = ?";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;
    private final BeanPropertyRowMapper<User> userMapper = new BeanPropertyRowMapper<>(User.class);

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, userMapper);
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = null;
        try {
            user = jdbcTemplate.queryForObject(SELECT_USER_SQL, userMapper, id);
        } catch (DataAccessException e) {
            log.debug("Wrong ID: {}, message: {}", id, e.getMessage());
        }
        return Optional.ofNullable(user);
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getEmail());
                    ps.setString(2, user.getLogin());
                    ps.setString(3, user.getName());
                    ps.setDate(4, Date.valueOf(user.getBirthday()));
                    return ps;
                }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public void updateUser(Long id, User user) {
        jdbcTemplate.update(UPDATE_USER_SQL, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), id);
    }

    @Override
    public boolean existsById(Long id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_EXIST_SQL, Boolean.class, id));
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_USER_SQL, id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbcTemplate.update(INSERT_FRIEND_SQL, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update(DELETE_FRIEND_SQL, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        return jdbcTemplate.query(SELECT_FRIENDS_SQL, userMapper, id);
    }
}
