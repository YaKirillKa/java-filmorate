package ru.yandex.practicum.filmorate.dao.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {

    /**
     * Returns all reviews.
     *
     * @return {@link List} of all review or empty {@link List}.
     */
    List<Review> findAll();

    List<Review> findLimit(int count);

    /**
     * Returns {@link Review} by the given id.
     *
     * @param id of the review to be returned.
     * @return {@link Review} wrapped in {@link Optional} or empty {@link Optional}.
     */
    Optional<Review> findById(Long id);

    /**
     * Sets the next available ID and saves the {@link Review} in storage.
     *
     * @param review the review to be saved.
     * @return saved {@link Review}.
     */
    Review createReview(Review review);

    /**
     * Saves the given {@link Review} by the given id.
     *
     * @param id of the review to be updated.
     * @param review the review to be saved.
     */
    void updateReview(Long id, Review review);

    /**
     * Checks whether there is a {@link Review} with the given id.
     *
     * @param id of the review to be checked.
     * @return true if the review exists or false.
     */
    boolean existsById(Long id);

    /**
     * Deletes the {@link Review} by the given id.
     *
     * @param id of the review to be removed.
     */
    void deleteById(Long id);

    void addLike(Optional<Review> review, Long reviewId, Long userId, Boolean isLike);

    void removeLike(Long reviewId, Long userId);

    boolean isLikeExist(Long reviewId, Long userId, Boolean isLike);


    List<Review> findByFilmId(Long filmId, int count);
}
