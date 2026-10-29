package com.example.splitmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.splitmate.data.local.entity.GroupEntity
import com.example.splitmate.data.local.entity.GroupMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertGroup(group: GroupEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMembers(
        members: List<GroupMemberEntity>
    )

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMember(
        member: GroupMemberEntity
    )

    @Update
    suspend fun updateGroup(group: GroupEntity): Int

    @Update
    suspend fun updateMember(
        member: GroupMemberEntity
    ): Int

    @Transaction
    suspend fun insertGroupWithMembers(
        group: GroupEntity,
        members: List<GroupMemberEntity>
    ) {
        insertGroup(group)
        insertMembers(members)
    }

    @Query(
        """
        SELECT * FROM groups
        ORDER BY createdAtEpochMillis DESC
        """
    )
    fun observeAllGroups(): Flow<List<GroupEntity>>

    @Query(
        """
        SELECT * FROM groups
        WHERE id = :groupId
        LIMIT 1
        """
    )
    fun observeGroup(
        groupId: String
    ): Flow<GroupEntity?>

    @Query(
        """
        SELECT * FROM group_members
        WHERE groupId = :groupId
        ORDER BY joinedAtEpochMillis ASC
        """
    )
    fun observeMembers(
        groupId: String
    ): Flow<List<GroupMemberEntity>>

    @Query(
        """
        DELETE FROM groups
        WHERE id = :groupId
        """
    )
    suspend fun deleteGroup(groupId: String): Int
}