package ru.yandex.practicum.filmorate.dao.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventDao {

    void addEvent(Event event);

    List<Event> getFeed(Long userId);
}
