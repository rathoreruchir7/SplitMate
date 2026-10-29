package com.example.splitmate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "groups",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["createdByUserId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["createdByUserId"])
    ]
)
data class GroupEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val createdByUserId: String,
    val createdAtEpochMillis: Long
)