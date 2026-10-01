package com.example.splitmate.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant

class ExpenseTest {

    private val createdAt =
        Instant.parse("2026-10-01T10:00:00Z")

    private fun createExpense(
        totalAmountInPaise: Long = 90_000,
        shares: List<ExpenseShare> = listOf(
            ExpenseShare("user-1", 30_000),
            ExpenseShare("user-2", 30_000),
            ExpenseShare("user-3", 30_000)
        )
    ): Expense {
        return Expense(
            id = "expense-1",
            groupId = "group-1",
            description = "Dinner",
            totalAmountInPaise = totalAmountInPaise,
            paidByUserId = "user-1",
            createdByUserId = "user-1",
            shares = shares,
            createdAt = createdAt
        )
    }

    @Test
    fun `creates expense when shares equal total amount`() {
        val expense = createExpense()

        assertEquals("expense-1", expense.id)
        assertEquals("Dinner", expense.description)
        assertEquals(90_000L, expense.totalAmountInPaise)
        assertEquals(3, expense.shares.size)
    }

    @Test
    fun `rejects expense when shares do not equal total amount`() {
        val invalidShares = listOf(
            ExpenseShare("user-1", 30_000),
            ExpenseShare("user-2", 30_000)
        )

        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            createExpense(
                totalAmountInPaise = 90_000,
                shares = invalidShares
            )
        }

        assertEquals(
            "Expense shares must equal the total amount",
            exception.message
        )
    }

    @Test
    fun `rejects expense containing duplicate shares`() {
        val duplicateShares = listOf(
            ExpenseShare("user-1", 45_000),
            ExpenseShare("user-1", 45_000)
        )

        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            createExpense(shares = duplicateShares)
        }

        assertEquals(
            "Expense cannot contain duplicate shares",
            exception.message
        )
    }

    @Test
    fun `rejects expense when shares are empty`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            createExpense(
                totalAmountInPaise = 90_000,
                shares = emptyList()
            )
        }

        assertEquals(
            "Expense must have at least one share",
            exception.message
        )
    }

    @Test
    fun `payer does not have to be a participant`() {
        val expense = Expense(
            id = "expense-2",
            groupId = "group-1",
            description = "Taxi",
            totalAmountInPaise = 60_000,
            paidByUserId = "user-1",
            createdByUserId = "user-2",
            shares = listOf(
                ExpenseShare("user-2", 30_000),
                ExpenseShare("user-3", 30_000)
            ),
            createdAt = createdAt
        )

        assertEquals("user-1", expense.paidByUserId)
        assertEquals(2, expense.shares.size)
    }

    @Test
    fun `expense protects its shares from external changes`() {
        val originalShares = mutableListOf(
            ExpenseShare("user-1", 45_000),
            ExpenseShare("user-2", 45_000)
        )

        val expense = createExpense(shares = originalShares)

        originalShares.clear()

        assertEquals(2, expense.shares.size)
    }
}