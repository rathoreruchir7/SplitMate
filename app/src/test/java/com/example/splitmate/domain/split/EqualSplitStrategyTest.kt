package com.example.splitmate.domain.split

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class EqualSplitStrategyTest {

    @Test
    fun `splits amount equally when there is no remainder`() {
        val shares = EqualSplitStrategy.calculateShares(
            totalAmountInPaise = 90_000,
            participantUserIds = listOf(
                "user-1",
                "user-2",
                "user-3"
            )
        )

        assertEquals(3, shares.size)
        assertEquals(30_000L, shares[0].amountInPaise)
        assertEquals(30_000L, shares[1].amountInPaise)
        assertEquals(30_000L, shares[2].amountInPaise)
    }

    @Test
    fun `distributes remaining paise deterministically`() {
        val shares = EqualSplitStrategy.calculateShares(
            totalAmountInPaise = 10_000,
            participantUserIds = listOf(
                "user-3",
                "user-1",
                "user-2"
            )
        )

        assertEquals("user-1", shares[0].userId)
        assertEquals(3_334L, shares[0].amountInPaise)

        assertEquals("user-2", shares[1].userId)
        assertEquals(3_333L, shares[1].amountInPaise)

        assertEquals("user-3", shares[2].userId)
        assertEquals(3_333L, shares[2].amountInPaise)

        assertEquals(
            10_000L,
            shares.sumOf { it.amountInPaise }
        )
    }

    @Test
    fun `produces same result regardless of participant order`() {
        val firstResult = EqualSplitStrategy.calculateShares(
            totalAmountInPaise = 10_000,
            participantUserIds = listOf(
                "user-1",
                "user-2",
                "user-3"
            )
        )

        val secondResult = EqualSplitStrategy.calculateShares(
            totalAmountInPaise = 10_000,
            participantUserIds = listOf(
                "user-3",
                "user-1",
                "user-2"
            )
        )

        assertEquals(firstResult, secondResult)
    }

    @Test
    fun `rejects duplicate participants`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            EqualSplitStrategy.calculateShares(
                totalAmountInPaise = 10_000,
                participantUserIds = listOf(
                    "user-1",
                    "user-1"
                )
            )
        }

        assertEquals(
            "Participants cannot contain duplicates",
            exception.message
        )
    }

    @Test
    fun `rejects empty participant list`() {
        assertThrows(IllegalArgumentException::class.java) {
            EqualSplitStrategy.calculateShares(
                totalAmountInPaise = 10_000,
                participantUserIds = emptyList()
            )
        }
    }

    @Test
    fun `rejects amount too small for participant count`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            EqualSplitStrategy.calculateShares(
                totalAmountInPaise = 2,
                participantUserIds = listOf(
                    "user-1",
                    "user-2",
                    "user-3"
                )
            )
        }

        assertEquals(
            "Amount is too small to split between all participants",
            exception.message
        )
    }
}