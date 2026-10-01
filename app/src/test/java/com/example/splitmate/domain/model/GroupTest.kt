package com.example.splitmate.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class GroupTest {

    private val joinedAt =
        Instant.parse("2026-10-01T10:00:00Z")

    private val admin = GroupMember(
        userId = "user-1",
        role = MemberRole.ADMIN,
        joinedAt = joinedAt
    )

    private val regularMember = GroupMember(
        userId = "user-2",
        role = MemberRole.MEMBER,
        joinedAt = joinedAt
    )

    private fun createGroup(): Group {
        return Group(
            id = "group-1",
            name = "Flatmates",
            createdByUserId = admin.userId,
            members = listOf(admin, regularMember),
            createdAt = joinedAt
        )
    }

    @Test
    fun `creates group when details are valid`() {
        val group = createGroup()

        assertEquals("group-1", group.id)
        assertEquals("Flatmates", group.name)
        assertEquals("user-1", group.createdByUserId)
        assertEquals(2, group.members.size)
    }

    @Test
    fun `admin can add a new member`() {
        val group = createGroup()

        val newMember = GroupMember(
            userId = "user-3",
            role = MemberRole.MEMBER,
            joinedAt = joinedAt
        )

        group.addMember(
            requestedByUserId = admin.userId,
            newMember = newMember
        )

        assertEquals(3, group.members.size)
        assertTrue(group.hasMember("user-3"))
    }

    @Test
    fun `regular member cannot add a new member`() {
        val group = createGroup()

        val newMember = GroupMember(
            userId = "user-3",
            role = MemberRole.MEMBER,
            joinedAt = joinedAt
        )

        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            group.addMember(
                requestedByUserId = regularMember.userId,
                newMember = newMember
            )
        }

        assertEquals(
            "Only a group admin can add members",
            exception.message
        )

        assertEquals(2, group.members.size)
    }

    @Test
    fun `rejects group containing duplicate members`() {
        val duplicateMember = GroupMember(
            userId = admin.userId,
            role = MemberRole.MEMBER,
            joinedAt = joinedAt
        )

        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            Group(
                id = "group-1",
                name = "Flatmates",
                createdByUserId = admin.userId,
                members = listOf(admin, duplicateMember),
                createdAt = joinedAt
            )
        }

        assertEquals(
            "Group cannot contain duplicate members",
            exception.message
        )
    }
}