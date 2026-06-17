package com.restall.studylink.data.user;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.List;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey
    @NonNull
    private String uid;
    private String email;
    private String name;
    private String photoUrl;
    private List<String> groupsIds;

    public UserEntity(String uid, String email, String name, String photoUrl, List<String> groupsIds) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.groupsIds = groupsIds;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public List<String> getGroupsIds() { return groupsIds; }
    public void setGroupsIds(List<String> groupsIds) { this.groupsIds = groupsIds; }
}