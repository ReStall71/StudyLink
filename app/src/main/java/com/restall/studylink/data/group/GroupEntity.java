package com.restall.studylink.data.group;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.List;

@Entity(tableName = "groups")
public class GroupEntity {
    @PrimaryKey
    @NonNull
    private String groupId;
    private String name;
    private String description;
    private String inviteCode;
    private String createdBy;
    private List<String> memberIds;
    private long createdAt;

    public GroupEntity() {}

    public GroupEntity(String groupId, String name, String description, String inviteCode,
                       String createdBy, List<String> memberIds, long createdAt) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.inviteCode = inviteCode;
        this.createdBy = createdBy;
        this.memberIds = memberIds;
        this.createdAt = createdAt;
    }

    // getters and setters
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public List<String> getMemberIds() { return memberIds; }
    public void setMemberIds(List<String> memberIds) { this.memberIds = memberIds; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}