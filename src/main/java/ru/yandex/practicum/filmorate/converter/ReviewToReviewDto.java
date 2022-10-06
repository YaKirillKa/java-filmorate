package ru.yandex.practicum.filmorate.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

@Component
public class ReviewToReviewDto implements Converter<Review, ReviewDto> {

    @Override
    public ReviewDto convert(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(review.getReviewId());
        reviewDto.setContent(review.getContent());
        reviewDto.setIsPositive(review.getIsPositive());
        reviewDto.setUserId(review.getUserId());
        reviewDto.setFilmId(review.getFilmId());
        reviewDto.setUseful(review.getUseful());
        return reviewDto;
    }
}
