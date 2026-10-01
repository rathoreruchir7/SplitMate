package com.example.splitmate.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant

class GroupMemberTest {

    @Test
    fun `creates group member when details are valid`() {
        val joinedAt = Instant.parse("2026-10-01T10:00:00Z")

        val member = GroupMember(
            userId = "user-1",
            role = MemberRole.ADMIN,
            joinedAt = joinedAt
        )

        assertEquals("user-1", member.userId)
        assertEquals(MemberRole.ADMIN, member.role)
        assertEquals(joinedAt, member.joinedAt)
    }

    @Test
    fun `rejects group member when user id is blank`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            GroupMember(
                userId = "   ",
                role = MemberRole.MEMBER,
                joinedAt = Instant.parse("2026-10-01T10:00:00Z")
            )
        }

        assertEquals(
            "User ID cannot be blank",
            exception.message
        )
    }
}