package com.example.splitmate.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String
) {
    init {
        require(id.isNotBlank()) {
            "User ID cannot be blank"
        }

        require(name.isNotBlank()) {
            "User name cannot be blank"
        }

        require(email.isNotBlank()) {
            "User email cannot be blank"
        }
    }
}
