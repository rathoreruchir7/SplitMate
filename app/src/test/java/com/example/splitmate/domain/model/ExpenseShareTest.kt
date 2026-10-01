package com.example.splitmate.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ExpenseShareTest {

    @Test
    fun `creates expense share when details are valid`() {
        val share = ExpenseShare(
            userId = "user-1",
            amountInPaise = 1050
        )

        assertEquals("user-1", share.userId)
        assertEquals(1050L, share.amountInPaise)
    }

    @Test
    fun `rejects expense share when user id is blank`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            ExpenseShare(
                userId = " ",
                amountInPaise = 1050
            )
        }

        assertEquals(
            "User ID cannot be blank",
            exception.message
        )
    }

    @Test
    fun `rejects expense share when amount is zero`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            ExpenseShare(
                userId = "user-1",
                amountInPaise = 0
            )
        }

        assertEquals(
            "Expense share must be greater than zero",
            exception.message
        )
    }

    @Test
    fun `rejects expense share when amount is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            ExpenseShare(
                userId = "user-1",
                amountInPaise = -100
            )
        }
    }
}