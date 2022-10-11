package ru.yandex.practicum.filmorate.dao.recommendation;

import java.util.List;

public interface RecommendationDao {

    List<Long> getEqualUserIds(Long id);

    List<Long> getAbsentFilms(Long id, List<Long> friendId);
}
