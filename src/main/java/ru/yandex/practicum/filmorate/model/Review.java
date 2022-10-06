package ru.yandex.practicum.filmorate.model;

public class Review {
    private Long reviewId;

    private String content;

    private Boolean isPositive;

    private Long userId;

    private Long filmId;

    private Long useful;

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsPositive() {
        return isPositive;
    }

    public void setIsPositive(Boolean isPositive) {
        this.isPositive = isPositive;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public Long getUseful() {
        return useful;
    }

    public void setUseful(Long useful) {
        this.useful = useful;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return getReviewId().equals(review.getReviewId());
    }

    @Override
    public int hashCode() {
        return getReviewId().hashCode();
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", content='" + content + '\'' +
                ", isPositive='" + isPositive + '\'' +
                ", userId=" + userId +
                ", filmId=" + filmId +
                ", useful=" + useful +
                '}';
    }
}
