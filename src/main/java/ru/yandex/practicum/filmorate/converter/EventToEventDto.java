package ru.yandex.practicum.filmorate.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

@Component
public class EventToEventDto implements Converter<Event, EventDto> {

    @Override
    public EventDto convert(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setEventId(event.getId());
        eventDto.setTimestamp(event.getCreated().getTime());
        eventDto.setUserId(event.getUserId());
        eventDto.setOperation(event.getOperation().name());
        eventDto.setEventType(event.getEventType().name());
        eventDto.setEntityId(event.getEntityId());
        return eventDto;
    }
}
