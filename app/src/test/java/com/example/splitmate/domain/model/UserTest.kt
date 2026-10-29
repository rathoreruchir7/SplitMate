package com.example.splitmate.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class UserTest {
    @Test
    fun `creates user when details are valid`() {
        val user = User(
            id = "user-1",
            name = "Ruchir",
            email = "ruchir@example.com"
        )

        assertEquals("user-1", user.id)
        assertEquals("Ruchir", user.name)
        assertEquals("ruchir@example.com", user.email)
    }

    @Test
    fun `rejects user when id is blank`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            User(
                id = "",
                name = "Ruchir",
                email = "ruchir@example.com"
            )
        }

        assertEquals(
            "User ID cannot be blank",
            exception.message
        )
    }
}