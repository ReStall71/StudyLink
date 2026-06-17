package com.restall.studylink.data.group;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroup(GroupEntity group);

    @Query("SELECT * FROM groups WHERE groupId = :groupId")
    GroupEntity getGroupById(String groupId);

    @Query("SELECT * FROM groups WHERE groupId IN (:groupIds)")
    List<GroupEntity> getGroupsByIds(List<String> groupIds);

    @Query("DELETE FROM groups WHERE groupId = :groupId")
    void deleteGroup(String groupId);
}