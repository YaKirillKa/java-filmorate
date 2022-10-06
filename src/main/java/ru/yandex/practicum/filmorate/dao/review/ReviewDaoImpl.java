package ru.yandex.practicum.filmorate.dao.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
    private static final String IS_EXIST_SQL = "SELECT EXISTS(SELECT * FROM review WHERE id = ?)";
    private static final String SELECT_ALL_SQL =
            "SELECT r.*, SUM(CASE WHEN l.is_like THEN 1 WHEN NOT l.is_like THEN -1 else 0 END) as useful " +
                    "FROM REVIEW r LEFT JOIN REVIEW_LIKES l ON l.review_id = r.id GROUP BY r.ID ORDER BY useful DESC, r.ID";

    private static final String SELECT_LIMIT_SQL =
            "SELECT r.*, SUM(CASE WHEN l.is_like THEN 1 WHEN NOT l.is_like THEN -1 else 0 END) as useful " +
                    "FROM REVIEW r LEFT JOIN REVIEW_LIKES l ON l.review_id = r.id GROUP BY r.ID ORDER BY useful DESC, r.ID LIMIT ?";

    private static final String SELECT_REVIEW_BY_FILM_SQL =
            "SELECT r.*, SUM(CASE WHEN l.is_like THEN 1 WHEN NOT l.is_like THEN -1 else 0 END) as useful " +
                    "FROM REVIEW r LEFT JOIN REVIEW_LIKES l ON l.review_id = r.id GROUP BY r.id HAVING r.film_id = ? " +
                    "ORDER BY useful DESC, r.id LIMIT ?";
    private static final String SELECT_REVIEW_BY_ID_SQL =
            "SELECT r.*, SUM(CASE WHEN l.is_like THEN 1 WHEN NOT l.is_like THEN -1 else 0 END) as useful " +
                    "FROM REVIEW r LEFT JOIN REVIEW_LIKES l ON l.review_id = r.id GROUP BY r.id HAVING r.id = ?";

    private static final String INSERT_REVIEW_SQL = "INSERT INTO review(content, is_positive, user_id, film_id)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_REVIEW_SQL = "UPDATE review SET content = ?, is_positive = ?, user_id = ?, film_id = ? " +
            "WHERE id = ?";
    private static final String SELECT_USEFUL_SQL =
            "SELECT SUM(CASE WHEN is_like THEN 1 WHEN NOT is_like THEN -1 else 0 END) " +
                    "FROM review_likes WHERE review_id = ?";

    private static final String INSERT_LIKE_SQL = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES(?, ?, ?)";

    private static final String DELETE_LIKE_SQL = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";

    private static final String DELETE_REVIEW_SQL = "DELETE FROM review WHERE id = ?";

    private static final String SELECT_LIKE_EXIST = "SELECT count(*) FROM review_likes WHERE is_like = ? AND review_id = ? AND user_id = ?";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Review> reviewMapper;

    @Autowired
    public ReviewDaoImpl(JdbcTemplate jdbcTemplate, RowMapper<Review> reviewMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public List<Review> findAll() {
        List<Review> reviews = jdbcTemplate.query(SELECT_ALL_SQL, reviewMapper);
        return reviews;
    }

    @Override
    public List<Review> findLimit(int count) {
        List<Review> reviews = jdbcTemplate.query(SELECT_LIMIT_SQL, reviewMapper, count);
        return reviews;
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
        review.setUseful(Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_USEFUL_SQL, Long.class,
                reviewId)).orElse(0L));
        return review;
    }

    @Override
    public void updateReview(Long id, Review review) {
        Optional<Review> oldReview = findById(id);
        if (oldReview.isPresent()) {
            jdbcTemplate.update(UPDATE_REVIEW_SQL,
                    review.getContent(), review.getIsPositive(),
                    oldReview.get().getUserId(), oldReview.get().getFilmId(), id);
        }
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
    public void addLike(Optional<Review> review, Long reviewId, Long userId, Boolean isLike) {
        jdbcTemplate.update(INSERT_LIKE_SQL, reviewId, userId, isLike);
        if (review.isPresent())
            updateReview(reviewId, review.get());
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        jdbcTemplate.update(DELETE_LIKE_SQL, reviewId, userId);
    }

    @Override
    public boolean isLikeExist(Long reviewId, Long userId, Boolean isLike) {
        return jdbcTemplate.queryForObject(SELECT_LIKE_EXIST, Integer.class, isLike, reviewId, userId) > 0;
    }
}
