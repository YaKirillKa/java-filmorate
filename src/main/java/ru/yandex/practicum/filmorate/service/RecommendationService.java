package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.dao.recommendation.RecommendationDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public class RecommendationService {

    private final RecommendationDao recommendationDao;
    private final FilmDao filmDao;

    public RecommendationService(RecommendationDao recommendationDao, FilmDao filmDao) {
        this.recommendationDao = recommendationDao;
        this.filmDao = filmDao;
    }

    public List<Film> getRecommendation(Long userId) {
        List<Long> equalUserId = recommendationDao.getEqualUserId(userId);
        List<Long> absentFilms = recommendationDao.getAbsentFilms(userId, equalUserId);
        return filmDao.toFilm(absentFilms);
    }
}
