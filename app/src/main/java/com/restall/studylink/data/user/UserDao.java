package com.restall.studylink.data.user;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    @Query("SELECT * FROM users WHERE uid = :uid")
    UserEntity getUserById(String uid);

    @Query("SELECT * FROM users WHERE uid IN (:uids)")
    List<UserEntity> getUsersByIds(List<String> uids);

    @Query("DELETE FROM users WHERE uid = :uid")
    void deleteUser(String uid);
}