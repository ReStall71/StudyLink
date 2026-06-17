package com.restall.studylink.data.schedule;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(EventEntity event);

    @Query("SELECT * FROM events WHERE groupId = :groupId ORDER BY startTime ASC")
    List<EventEntity> getEventsByGroup(String groupId);

    @Query("SELECT * FROM events WHERE eventId = :eventId")
    EventEntity getEventById(String eventId);

    @Query("DELETE FROM events WHERE eventId = :eventId")
    void deleteEvent(String eventId);
}