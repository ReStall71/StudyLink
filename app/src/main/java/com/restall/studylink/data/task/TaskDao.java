package com.restall.studylink.data.task;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(TaskEntity task);

    @Update
    void updateTask(TaskEntity task);

    @Query("SELECT * FROM tasks WHERE groupId = :groupId ORDER BY dueDate ASC")
    List<TaskEntity> getTasksByGroup(String groupId);

    @Query("SELECT * FROM tasks WHERE assignedTo = :uid")
    List<TaskEntity> getTasksAssignedTo(String uid);

    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    void deleteTask(String taskId);
}