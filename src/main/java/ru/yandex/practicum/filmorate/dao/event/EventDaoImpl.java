package ru.yandex.practicum.filmorate.dao.event;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Component
public class EventDaoImpl implements EventDao {

    private static String INSERT_EVENT_SQL = "INSERT INTO event(user_id, event_type, operation, entity_id)" +
            "VALUES ( ?, ?, ?, ? )";

    private static String SELECT_EVENT_SQL = "SELECT * FROM event WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public EventDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addEvent(Event event) {
        jdbcTemplate.update(INSERT_EVENT_SQL,
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId());
    }

    @Override
    public List<Event> getFeed(Long userId) {
        return jdbcTemplate.query(SELECT_EVENT_SQL, new BeanPropertyRowMapper<>(Event.class), userId);
    }
}
