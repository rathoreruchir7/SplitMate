package com.example.splitmate.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
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
            childColumns = ["payerUserId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipientUserId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["resolvedByUserId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["payerUserId"]),
        Index(value = ["recipientUserId"]),
        Index(value = ["resolvedByUserId"])
    ]
)
data class PaymentEntity(
    @PrimaryKey
    val id: String,
    val groupId: String,
    val payerUserId: String,
    val recipientUserId: String,
    val amountInPaise: Long,
    val status: String,
    val createdAtEpochMillis: Long,
    val resolvedAtEpochMillis: Long?,
    val resolvedByUserId: String?
)