package com.restall.studylink.data.group;

import android.content.Context;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.restall.studylink.data.AppDatabase;
import com.restall.studylink.utils.FirebaseManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupRepository {

    private final GroupDao groupDao;
    private final FirebaseManager firebaseManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public GroupRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        groupDao = db.groupDao();
        firebaseManager = new FirebaseManager();
    }

    void insertGroup(GroupEntity group) {
        executor.execute(() -> groupDao.insertGroup(group));
    }

    void getGroupById(String groupId, LocalGroupCallback callback) {
        executor.execute(() -> {
            GroupEntity group = groupDao.getGroupById(groupId);
            if (callback != null) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> callback.onGroupLoaded(group));
            }
        });
    }
    public void syncUserGroups(String userId, final SyncCallback callback) {
        firebaseManager.getUserGroups(userId, new FirebaseManager.GroupsListCallback() {
            @Override
            public void onSuccess(List<GroupEntity> groups) {
                executor.execute(() -> {
                    for (GroupEntity group : groups) {
                        groupDao.insertGroup(group);
                    }
                    if (callback != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(callback::onSyncComplete);
                    }
                });
            }
            @Override
            public void onFailure(String error) {
                if (callback != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> callback.onError(error));
                }
            }
        });
    }

    List<GroupEntity> getGroupsByIds(List<String> groupIds){
        return groupDao.getGroupsByIds(groupIds);
    }

    void deleteGroup(String groupId){
        groupDao.deleteGroup(groupId);
    }

    public interface LocalGroupCallback {
        void onGroupLoaded(GroupEntity group);
    }

    public interface SyncCallback {
        void onSyncComplete();
        void onError(String error);
    }

}
