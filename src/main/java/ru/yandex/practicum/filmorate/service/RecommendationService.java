package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.dao.recommendation.RecommendationDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RecommendationDao recommendationDao;
    private final FilmDao filmDao;

    public RecommendationService(RecommendationDao recommendationDao, FilmDao filmDao) {
        this.recommendationDao = recommendationDao;
        this.filmDao = filmDao;
    }

    public List<Film> getRecommendations(Long userId) {
        List<Long> equalUserIds = recommendationDao.getEqualUserIds(userId);
        return recommendationDao.getAbsentFilms(userId, equalUserIds).stream()
                .map(filmDao::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
