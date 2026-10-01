package com.example.splitmate.domain.model

import java.time.Instant

class Group(
    val id: String,
    val name: String,
    val createdByUserId: String,
    members: List<GroupMember>,
    val createdAt: Instant
) {
    private val memberList: MutableList<GroupMember> =
        members.toMutableList()

    val members: List<GroupMember>
        get() = memberList.toList()

    init {
        require(id.isNotBlank()) {
            "Group ID cannot be blank"
        }

        require(name.isNotBlank()) {
            "Group name cannot be blank"
        }

        require(createdByUserId.isNotBlank()) {
            "Creator user ID cannot be blank"
        }

        require(memberList.isNotEmpty()) {
            "Group must have at least one member"
        }

        require(memberList.map { it.userId }.distinct().size == memberList.size) {
            "Group cannot contain duplicate members"
        }

        val creatorMembership = memberList.find {
            it.userId == createdByUserId
        }

        requireNotNull(creatorMembership) {
            "Group creator must be a member"
        }

        require(creatorMembership.role == MemberRole.ADMIN) {
            "Group creator must be an admin"
        }
    }

    fun hasMember(userId: String): Boolean {
        return memberList.any { member ->
            member.userId == userId
        }
    }

    fun addMember(
        requestedByUserId: String,
        newMember: GroupMember
    ) {
        val requester = memberList.find {
            it.userId == requestedByUserId
        }

        require(requester?.role == MemberRole.ADMIN) {
            "Only a group admin can add members"
        }

        require(!hasMember(newMember.userId)) {
            "User is already a group member"
        }

        memberList.add(newMember)
    }
}