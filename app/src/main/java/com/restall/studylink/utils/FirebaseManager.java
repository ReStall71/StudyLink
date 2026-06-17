package com.restall.studylink.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.restall.studylink.data.group.GroupEntity;
import com.restall.studylink.data.user.UserEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private final FirebaseAuth auth;
    private final DatabaseReference usersRef;
    private final DatabaseReference groupsRef;

    public FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        groupsRef = FirebaseDatabase.getInstance().getReference("groups");
    }

    public void registerUser(String email, String password, String name, final AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            saveUserToDatabase(uid, email, name, callback);
                        } else {
                            callback.onFailure("Ошибка получения пользователя");
                        }
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Ошибка регистрации");
                    }
                });
    }

    private void saveUserToDatabase(String uid, String email, String name, AuthCallback callback) {
        UserEntity user = new UserEntity(uid, email, name, null, new ArrayList<>());
        usersRef.child(uid).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("Ошибка сохранения профиля");
                    }
                });
    }

    public void loginUser(String email, String password, final AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Ошибка входа");
                    }
                });
    }

    public void logout() {
        auth.signOut();
    }

    public void delete_account() {
        auth.getCurrentUser().delete().addOnCompleteListener(task -> {logout();});
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void getUserProfile(String uid, final ProfileCallback callback) {
        usersRef.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                UserEntity user = task.getResult().getValue(UserEntity.class);
                callback.onSuccess(user);
            } else {
                callback.onFailure("Не удалось загрузить профиль");
            }
        });
    }

    public void updateUserName(String uid, String newName, final UpdateCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        usersRef.child(uid).updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("Ошибка обновления");
                    }
                });
    }

    public void createGroup(String groupName, String description, String creatorUid, final GroupCallback callback) {
        String groupId = groupsRef.push().getKey();
        if (groupId == null) {
            callback.onFailure("Ошибка генерации ID");
            return;
        }
        String inviteCode = generateInviteCode(); // метод ниже
        List<String> members = new ArrayList<>();
        members.add(creatorUid);
        GroupEntity group = new GroupEntity(groupId, groupName, description, inviteCode, creatorUid, members, System.currentTimeMillis());
        groupsRef.child(groupId).setValue(group)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addGroupToUser(creatorUid, groupId, callback);
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Ошибка создания");
                    }
                });
    }

    private String generateInviteCode() {
        return String.valueOf(System.currentTimeMillis()).substring(5) + (int)(Math.random() * 1000);
    }

    private void addGroupToUser(String uid, String groupId, final GroupCallback callback) {
        usersRef.child(uid).child("groupsIds").get().addOnCompleteListener(task -> {
            List<String> groupsIds = new ArrayList<>();
            if (task.isSuccessful() && task.getResult().exists()) {
                // Получаем текущий список
                Object value = task.getResult().getValue();
                if (value instanceof List) {
                    groupsIds = (List<String>) value;
                }
            }
            if (!groupsIds.contains(groupId)) {
                groupsIds.add(groupId);
            }
            usersRef.child(uid).child("groupsIds").setValue(groupsIds)
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            callback.onSuccess(groupId);
                        } else {
                            callback.onFailure("Группа создана, но не добавлена в профиль");
                        }
                    });
        });
    }

    public void joinGroupByCode(String inviteCode, String userId, final GroupCallback callback) {
        groupsRef.orderByChild("inviteCode").equalTo(inviteCode).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DataSnapshot snapshot = task.getResult().getChildren().iterator().next();
                        String groupId = snapshot.getKey();
                        GroupEntity group = snapshot.getValue(GroupEntity.class);
                        if (group != null && !group.getMemberIds().contains(userId)) {
                            List<String> members = group.getMemberIds();
                            members.add(userId);
                            groupsRef.child(groupId).child("memberIds").setValue(members)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            addGroupToUser(userId, groupId, callback);
                                        } else {
                                            callback.onFailure("Не удалось добавить участника");
                                        }
                                    });
                        } else if (group != null && group.getMemberIds().contains(userId)) {
                            callback.onFailure("Вы уже состоите в этой группе");
                        } else {
                            callback.onFailure("Ошибка получения группы");
                        }
                    } else {
                        callback.onFailure("Неверный код приглашения");
                    }
                });
    }

    public void getUserGroups(String userId, final GroupsListCallback callback) {
        usersRef.child(userId).child("groupsIds").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        List<String> groupsIds = (List<String>) task.getResult().getValue();
                        if (groupsIds == null || groupsIds.isEmpty()) {
                            callback.onSuccess(new ArrayList<>());
                            return;
                        }
                        groupsRef.get().addOnCompleteListener(taskAll -> {
                            if (taskAll.isSuccessful()) {
                                List<GroupEntity> groups = new ArrayList<>();
                                for (DataSnapshot snapshot : taskAll.getResult().getChildren()) {
                                    GroupEntity group = snapshot.getValue(GroupEntity.class);
                                    if (group != null && groupsIds.contains(group.getGroupId())) {
                                        groups.add(group);
                                    }
                                }
                                callback.onSuccess(groups);
                            } else {
                                callback.onFailure("Ошибка загрузки групп");
                            }
                        });
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                });
    }

    public void getGroupById(String groupId, final GroupCallback callback) {
        groupsRef.child(groupId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                GroupEntity group = task.getResult().getValue(GroupEntity.class);
                callback.onSuccessGroup(group);
            } else {
                callback.onFailure("Группа не найдена");
            }
        });
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface ProfileCallback {
        void onSuccess(UserEntity user);
        void onFailure(String error);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface GroupCallback {
        void onSuccess(String groupId);
        void onSuccessGroup(GroupEntity group);
        void onFailure(String error);
    }

    public interface GroupsListCallback {
        void onSuccess(List<GroupEntity> groups);
        void onFailure(String error);
    }

}