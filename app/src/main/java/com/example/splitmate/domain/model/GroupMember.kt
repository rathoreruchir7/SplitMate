package com.example.splitmate.domain.model

import java.time.Instant

data class GroupMember(
    val userId: String,
    val role: MemberRole,
    val joinedAt: Instant
) {
    init {
        require(userId.isNotBlank()) {
            "User ID cannot be blank"
        }
    }
}
