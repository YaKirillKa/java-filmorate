package ru.yandex.practicum.filmorate.dao.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RecommendationDaoImpl implements RecommendationDao {

    private static final String SELECT_EQUAL_USERS_ID_SQL =
            "SELECT user_id " +
            "FROM film_likes " +
            "WHERE user_id != :id AND EXISTS (SELECT film_id " +
            "              FROM film_likes " +
            "              WHERE user_id = :id) " +
            "GROUP BY user_id";

    private static final String SELECT_ABSENT_FILM_SQL =
            "SELECT film_id " +
            "FROM film_likes " +
            "WHERE user_id IN (:ids) AND " +
            "      film_id NOT IN (SELECT film_id " +
            "                  FROM film_likes " +
            "                  WHERE user_id = :id) " +
            "GROUP BY film_id";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public RecommendationDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Long> getEqualUserIds(Long id) {
        Map<String, Long> parameters = new HashMap<>();
        parameters.put("id", id);
        return namedParameterJdbcTemplate.queryForList(SELECT_EQUAL_USERS_ID_SQL,
                parameters, Long.class);
    }

    @Override
    public List<Long> getAbsentFilms(Long id, List<Long> friendId){
        MapSqlParameterSource parameters = new MapSqlParameterSource("ids", friendId);
        parameters.addValue("id", id);
        return namedParameterJdbcTemplate.queryForList(SELECT_ABSENT_FILM_SQL,
                parameters, Long.class);
    }
}
