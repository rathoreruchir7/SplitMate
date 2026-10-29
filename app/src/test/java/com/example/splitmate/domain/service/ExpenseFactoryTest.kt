package com.example.splitmate.domain.service

import com.example.splitmate.domain.model.Group
import com.example.splitmate.domain.model.GroupMember
import com.example.splitmate.domain.model.MemberRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant

class ExpenseFactoryTest {

    private val timestamp =
        Instant.parse("2026-10-02T10:00:00Z")

    private val group = Group(
        id = "group-1",
        name = "Flatmates",
        createdByUserId = "user-1",
        members = listOf(
            GroupMember(
                userId = "user-1",
                role = MemberRole.ADMIN,
                joinedAt = timestamp
            ),
            GroupMember(
                userId = "user-2",
                role = MemberRole.MEMBER,
                joinedAt = timestamp
            ),
            GroupMember(
                userId = "user-3",
                role = MemberRole.MEMBER,
                joinedAt = timestamp
            )
        ),
        createdAt = timestamp
    )

    @Test
    fun `creates equal expense for group members`() {
        val expense = ExpenseFactory.createEqualExpense(
            expenseId = "expense-1",
            group = group,
            description = "Dinner",
            totalAmountInPaise = 90_000,
            paidByUserId = "user-1",
            createdByUserId = "user-2",
            participantUserIds = listOf(
                "user-1",
                "user-2",
                "user-3"
            ),
            createdAt = timestamp
        )

        assertEquals("expense-1", expense.id)
        assertEquals("group-1", expense.groupId)
        assertEquals(3, expense.shares.size)

        assertEquals(
            90_000L,
            expense.shares.sumOf { it.amountInPaise }
        )
    }

    @Test
    fun `creates exact expense for group members`() {
        val expense = ExpenseFactory.createExactExpense(
            expenseId = "expense-2",
            group = group,
            description = "Groceries",
            totalAmountInPaise = 10_000,
            paidByUserId = "user-1",
            createdByUserId = "user-2",
            amountsByUserId = mapOf(
                "user-1" to 5_000L,
                "user-2" to 3_000L,
                "user-3" to 2_000L
            ),
            createdAt = timestamp
        )

        assertEquals(3, expense.shares.size)
        assertEquals(5_000L, expense.shares[0].amountInPaise)
        assertEquals(3_000L, expense.shares[1].amountInPaise)
        assertEquals(2_000L, expense.shares[2].amountInPaise)
    }

    @Test
    fun `rejects expense when payer is not a group member`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            ExpenseFactory.createEqualExpense(
                expenseId = "expense-3",
                group = group,
                description = "Dinner",
                totalAmountInPaise = 60_000,
                paidByUserId = "outsider",
                createdByUserId = "user-1",
                participantUserIds = listOf(
                    "user-1",
                    "user-2"
                ),
                createdAt = timestamp
            )
        }

        assertEquals(
            "Payer must be a group member",
            exception.message
        )
    }

    @Test
    fun `rejects expense when creator is not a group member`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            ExpenseFactory.createEqualExpense(
                expenseId = "expense-4",
                group = group,
                description = "Dinner",
                totalAmountInPaise = 60_000,
                paidByUserId = "user-1",
                createdByUserId = "outsider",
                participantUserIds = listOf(
                    "user-1",
                    "user-2"
                ),
                createdAt = timestamp
            )
        }

        assertEquals(
            "Expense creator must be a group member",
            exception.message
        )
    }

    @Test
    fun `rejects expense when participant is not a group member`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            ExpenseFactory.createEqualExpense(
                expenseId = "expense-5",
                group = group,
                description = "Dinner",
                totalAmountInPaise = 60_000,
                paidByUserId = "user-1",
                createdByUserId = "user-1",
                participantUserIds = listOf(
                    "user-1",
                    "outsider"
                ),
                createdAt = timestamp
            )
        }

        assertEquals(
            "All expense participants must be group members",
            exception.message
        )
    }
}