package com.restall.studylink.data.schedule;

import androidx.room.Query;

import java.util.List;

public class EventRepository {
    private final EventDao eventDao;

    public EventRepository(EventDao eventDao) {this.eventDao = eventDao;}

    void insertEvent(EventEntity event){
        eventDao.insertEvent(event);
    }

    List<EventEntity> getEventsByGroup(String groupId){
        return eventDao.getEventsByGroup(groupId);
    }

    EventEntity getEventById(String eventId){
        return eventDao.getEventById(eventId);
    }

    void deleteEvent(String eventId){
        eventDao.deleteEvent(eventId);
    }
}
