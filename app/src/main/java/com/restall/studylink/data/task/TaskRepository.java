package com.restall.studylink.data.task;

import java.util.List;

public class TaskRepository {
    private final TaskDao taskDao;

    public TaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public void insertTask(TaskEntity task) {
        taskDao.insertTask(task);
    }

    public void updateTask(TaskEntity task) {
        taskDao.updateTask(task);
    }

    public List<TaskEntity> getTasksByGroup(String groupId) {
        return taskDao.getTasksByGroup(groupId);
    }

    public List<TaskEntity> getTasksAssignedTo(String uid) {
        return taskDao.getTasksAssignedTo(uid);
    }

    public void deleteTask(String taskId) {
        taskDao.deleteTask(taskId);
    }
}