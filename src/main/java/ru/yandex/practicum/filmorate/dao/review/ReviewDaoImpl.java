package ru.yandex.practicum.filmorate.dao.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ReviewDaoImpl implements ReviewDao {

    private static final String SELECT_DEFAULT_REVIEWS_SQL = "SELECT r.id as reviewId, r.content, r.is_positive, " +
            "r.user_id, r.film_id, SUM(CASE WHEN l.is_like THEN 1 WHEN NOT l.is_like THEN -1 else 0 END) as useful " +
            "FROM review r LEFT JOIN review_likes l ON l.review_id = r.id ";
    private static final String SELECT_REVIEW_BY_ID_SQL = SELECT_DEFAULT_REVIEWS_SQL +
            "WHERE r.id = nvl(?, r.id) GROUP BY r.id";
    private static final String SELECT_REVIEW_BY_FILM_SQL = SELECT_DEFAULT_REVIEWS_SQL +
            "WHERE r.film_id = nvl(?, r.film_id) GROUP BY r.id ORDER BY useful DESC, r.id LIMIT ?";
    private static final String INSERT_REVIEW_SQL = "INSERT INTO review(content, is_positive, user_id, film_id)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_REVIEW_SQL = "UPDATE review SET content = ?, is_positive = ?, user_id = ?, " +
            "film_id = ? WHERE id = ?";
    private static final String INSERT_LIKE_SQL = "INSERT INTO review_likes (review_id, user_id, is_like) " +
            "VALUES(?, ?, ?)";
    private static final String DELETE_LIKE_SQL = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String DELETE_REVIEW_SQL = "DELETE FROM review WHERE id = ?";
    private static final String IS_EXIST_SQL = "SELECT EXISTS(SELECT * FROM review WHERE id = ?)";
    private static final String SELECT_LIKE_EXIST_SQL = "SELECT EXISTS(SELECT * FROM review_likes " +
            "WHERE is_like = ? AND review_id = ? AND user_id = ?)";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    private final BeanPropertyRowMapper<Review> reviewMapper = new BeanPropertyRowMapper<>(Review.class);

    @Autowired
    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> findByFilmId(Long filmId, int count) {
        return jdbcTemplate.query(SELECT_REVIEW_BY_FILM_SQL, reviewMapper, filmId, count);
    }

    @Override
    public Optional<Review> findById(Long id) {
        Review review = null;
        try {
            review = jdbcTemplate.queryForObject(SELECT_REVIEW_BY_ID_SQL, reviewMapper, id);
        } catch (DataAccessException e) {
            log.debug("Wrong ID: {}, message: {}", id, e.getMessage());
        }
        return Optional.ofNullable(review);
    }

    @Override
    public Review createReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement(INSERT_REVIEW_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);
        Long reviewId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        review.setReviewId(reviewId);
        review.setUseful(0L);
        return review;
    }

    @Override
    public void updateReview(Long id, Review review) {
        jdbcTemplate.update(UPDATE_REVIEW_SQL, review.getContent(), review.getIsPositive(),
                review.getUserId(), review.getFilmId(), id);
    }

    @Override
    public boolean existsById(Long id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_EXIST_SQL, Boolean.class, id));
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_REVIEW_SQL, id);
    }

    @Override
    public void addLike(Long reviewId, Long userId, Boolean isLike) {
        jdbcTemplate.update(INSERT_LIKE_SQL, reviewId, userId, isLike);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        jdbcTemplate.update(DELETE_LIKE_SQL, reviewId, userId);
    }

    @Override
    public boolean isLikeExist(Long reviewId, Long userId, Boolean isLike) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(SELECT_LIKE_EXIST_SQL, Boolean.class,
                isLike, reviewId, userId));
    }
}
