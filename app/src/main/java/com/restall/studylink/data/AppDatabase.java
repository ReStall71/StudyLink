package com.restall.studylink.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.restall.studylink.data.group.GroupDao;
import com.restall.studylink.data.group.GroupEntity;
import com.restall.studylink.data.schedule.EventDao;
import com.restall.studylink.data.schedule.EventEntity;
import com.restall.studylink.data.task.TaskDao;
import com.restall.studylink.data.task.TaskEntity;
import com.restall.studylink.data.user.UserDao;
import com.restall.studylink.data.user.UserEntity;

@Database(
        entities = {UserEntity.class, GroupEntity.class, EventEntity.class, TaskEntity.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract GroupDao groupDao();
    public abstract EventDao eventDao();
    public abstract TaskDao taskDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "studylink_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}