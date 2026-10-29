package com.example.splitmate.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(
            value = ["email"],
            unique = true
        )
    ]
)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String
)