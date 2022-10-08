package ru.yandex.practicum.filmorate.model;

import java.sql.Timestamp;

public class Event {
    private Long id;
    private Timestamp created;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;

    public Event() {}

    public Event(Long userId, EventType eventType, Operation operation, Long entityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return getId().equals(event.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", created=" + created +
                ", userId=" + userId +
                ", eventType=" + eventType +
                ", operation=" + operation +
                ", entityId=" + entityId +
                '}';
    }

    public enum EventType {
        LIKE, REVIEW, FRIEND
    }

    public enum Operation {
        REMOVE, ADD, UPDATE
    }
}
