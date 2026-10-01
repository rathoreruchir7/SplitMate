package com.example.splitmate.domain.service

import com.example.splitmate.domain.model.Expense
import com.example.splitmate.domain.model.Group
import com.example.splitmate.domain.model.GroupMember
import com.example.splitmate.domain.model.MemberRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant

class BalanceCalculatorTest {

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

    private fun createEqualExpense(
        expenseId: String,
        totalAmountInPaise: Long,
        paidByUserId: String,
        participantUserIds: List<String>
    ): Expense {
        return ExpenseFactory.createEqualExpense(
            expenseId = expenseId,
            group = group,
            description = "Test expense",
            totalAmountInPaise = totalAmountInPaise,
            paidByUserId = paidByUserId,
            createdByUserId = "user-1",
            participantUserIds = participantUserIds,
            createdAt = timestamp
        )
    }

    @Test
    fun `returns zero balances when there are no expenses`() {
        val balances = BalanceCalculator.calculate(
            group = group,
            expenses = emptyList()
        )

        assertEquals(
            mapOf(
                "user-1" to 0L,
                "user-2" to 0L,
                "user-3" to 0L
            ),
            balances
        )
    }

    @Test
    fun `calculates balances for one equal expense`() {
        val dinner = createEqualExpense(
            expenseId = "expense-1",
            totalAmountInPaise = 90_000,
            paidByUserId = "user-1",
            participantUserIds = listOf(
                "user-1",
                "user-2",
                "user-3"
            )
        )

        val balances = BalanceCalculator.calculate(
            group = group,
            expenses = listOf(dinner)
        )

        assertEquals(60_000L, balances["user-1"])
        assertEquals(-30_000L, balances["user-2"])
        assertEquals(-30_000L, balances["user-3"])
    }

    @Test
    fun `calculates balances across multiple expenses`() {
        val dinner = createEqualExpense(
            expenseId = "expense-1",
            totalAmountInPaise = 90_000,
            paidByUserId = "user-1",
            participantUserIds = listOf(
                "user-1",
                "user-2",
                "user-3"
            )
        )

        val taxi = createEqualExpense(
            expenseId = "expense-2",
            totalAmountInPaise = 60_000,
            paidByUserId = "user-2",
            participantUserIds = listOf(
                "user-1",
                "user-2"
            )
        )

        val balances = BalanceCalculator.calculate(
            group = group,
            expenses = listOf(dinner, taxi)
        )

        assertEquals(30_000L, balances["user-1"])
        assertEquals(0L, balances["user-2"])
        assertEquals(-30_000L, balances["user-3"])
    }

    @Test
    fun `supports payer who is not a participant`() {
        val taxi = createEqualExpense(
            expenseId = "expense-1",
            totalAmountInPaise = 60_000,
            paidByUserId = "user-1",
            participantUserIds = listOf(
                "user-2",
                "user-3"
            )
        )

        val balances = BalanceCalculator.calculate(
            group = group,
            expenses = listOf(taxi)
        )

        assertEquals(60_000L, balances["user-1"])
        assertEquals(-30_000L, balances["user-2"])
        assertEquals(-30_000L, balances["user-3"])
    }

    @Test
    fun `rejects duplicate expenses`() {
        val dinner = createEqualExpense(
            expenseId = "expense-1",
            totalAmountInPaise = 90_000,
            paidByUserId = "user-1",
            participantUserIds = listOf(
                "user-1",
                "user-2",
                "user-3"
            )
        )

        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            BalanceCalculator.calculate(
                group = group,
                expenses = listOf(dinner, dinner)
            )
        }

        assertEquals(
            "Expenses cannot contain duplicates",
            exception.message
        )
    }
}