package com.example.splitmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.splitmate.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(users: List<UserEntity>)

    @Update
    suspend fun update(user: UserEntity): Int

    @Query(
        """
        SELECT * FROM users
        WHERE id = :userId
        LIMIT 1
        """
    )
    suspend fun getById(userId: String): UserEntity?

    @Query(
        """
        SELECT * FROM users
        WHERE id = :userId
        LIMIT 1
        """
    )
    fun observeById(userId: String): Flow<UserEntity?>

    @Query(
        """
        SELECT * FROM users
        ORDER BY name COLLATE NOCASE ASC
        """
    )
    fun observeAll(): Flow<List<UserEntity>>
}