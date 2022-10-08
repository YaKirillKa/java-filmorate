package ru.yandex.practicum.filmorate.controller;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ConversionService conversionService;
    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewService reviewService, ConversionService conversionService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.conversionService = conversionService;
        this.reviewMapper = reviewMapper;
    }

    @GetMapping
    public List<ReviewDto> findAll(@RequestParam(required = false) Long filmId,
                                   @RequestParam(defaultValue = "10", required = false) Integer count) {
        return reviewService.findAll(filmId, count).stream()
                .map(review -> conversionService.convert(review, ReviewDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ReviewDto findById(@PathVariable Long id) {
        Review review = reviewService.findById(id);
        return conversionService.convert(review, ReviewDto.class);
    }

    @DeleteMapping("/{id}")
    public void removeReview(@PathVariable Long id) {
        reviewService.removeReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@Valid @RequestBody(required = false) ReviewDto reviewDto, @PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLike(reviewMapper.mapToReview(reviewDto), id, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@Valid @RequestBody(required = false) ReviewDto reviewDto, @PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLike(reviewMapper.mapToReview(reviewDto), id, userId, false);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLike(id, userId, true);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLike(id, userId, false);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReviewDto create(@Valid @NotNull @RequestBody ReviewDto reviewDto) {
        Review review = reviewMapper.mapToReview(reviewDto);
        review = reviewService.create(review);
        return conversionService.convert(review, ReviewDto.class);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReviewDto update(@Valid @NotNull @RequestBody ReviewDto reviewDto) {
        Review review = reviewMapper.mapToReview(reviewDto);
        review = reviewService.update(review.getReviewId(), review);
        return conversionService.convert(review, ReviewDto.class);
    }
}
