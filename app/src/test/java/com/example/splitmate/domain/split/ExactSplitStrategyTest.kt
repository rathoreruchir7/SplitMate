package com.example.splitmate.domain.split

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ExactSplitStrategyTest {

    @Test
    fun `creates exact shares when amounts equal total`() {
        val shares = ExactSplitStrategy.calculateShares(
            totalAmountInPaise = 10_000,
            amountsByUserId = mapOf(
                "user-1" to 5_000L,
                "user-2" to 3_000L,
                "user-3" to 2_000L
            )
        )

        assertEquals(3, shares.size)

        assertEquals("user-1", shares[0].userId)
        assertEquals(5_000L, shares[0].amountInPaise)

        assertEquals("user-2", shares[1].userId)
        assertEquals(3_000L, shares[1].amountInPaise)

        assertEquals("user-3", shares[2].userId)
        assertEquals(2_000L, shares[2].amountInPaise)
    }

    @Test
    fun `supports generic split strategy contract`() {
        val strategy: SplitStrategy<ExactSplitInput> =
            ExactSplitStrategy

        val shares = strategy.calculateShares(
            totalAmountInPaise = 10_000,
            input = ExactSplitInput(
                amountsByUserId = mapOf(
                    "user-1" to 6_000L,
                    "user-2" to 4_000L
                )
            )
        )

        assertEquals(2, shares.size)
        assertEquals(10_000L, shares.sumOf { it.amountInPaise })
    }

    @Test
    fun `rejects exact shares when amounts do not equal total`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            ExactSplitStrategy.calculateShares(
                totalAmountInPaise = 10_000,
                amountsByUserId = mapOf(
                    "user-1" to 5_000L,
                    "user-2" to 4_000L
                )
            )
        }

        assertEquals(
            "Exact shares must equal the total amount",
            exception.message
        )
    }

    @Test
    fun `rejects empty exact shares`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            ExactSplitStrategy.calculateShares(
                totalAmountInPaise = 10_000,
                amountsByUserId = emptyMap()
            )
        }

        assertEquals(
            "At least one participant is required",
            exception.message
        )
    }

    @Test
    fun `rejects zero exact share`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            ExactSplitStrategy.calculateShares(
                totalAmountInPaise = 10_000,
                amountsByUserId = mapOf(
                    "user-1" to 10_000L,
                    "user-2" to 0L
                )
            )
        }

        assertEquals(
            "Every exact share must be greater than zero",
            exception.message
        )
    }

    @Test
    fun `rejects blank participant id`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            ExactSplitStrategy.calculateShares(
                totalAmountInPaise = 10_000,
                amountsByUserId = mapOf(
                    "" to 5_000L,
                    "user-2" to 5_000L
                )
            )
        }

        assertEquals(
            "Participant user ID cannot be blank",
            exception.message
        )
    }

}