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
            "SELECT USER_ID " +
            "FROM FILM_LIKES " +
            "WHERE USER_ID != :id AND EXISTS (SELECT FILM_ID " +
            "              FROM FILM_LIKES " +
            "              WHERE USER_ID = :id) " +
            "GROUP BY USER_ID";

    private static final String SELECT_ABSENT_FILM_SQL =
            "SELECT FILM_ID " +
            "FROM FILM_LIKES " +
            "WHERE USER_ID IN (:ids) AND " +
            "      FILM_ID NOT IN (SELECT FILM_ID " +
            "                  FROM FILM_LIKES " +
            "                  WHERE USER_ID = :id) " +
            "GROUP BY FILM_ID";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public RecommendationDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Long> getEqualUserId(Long id) {
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
