package com.example.splitmate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["paidByUserId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["createdByUserId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["paidByUserId"]),
        Index(value = ["createdByUserId"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val groupId: String,
    val description: String,
    val totalAmountInPaise: Long,
    val paidByUserId: String,
    val createdByUserId: String,
    val createdAtEpochMillis: Long
)