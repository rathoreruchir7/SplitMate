package com.example.splitmate.domain.split

sealed interface SplitInput

data class EqualSplitInput(
    val participantUserIds: List<String>
) : SplitInput

data class ExactSplitInput(
    val amountsByUserId: Map<String, Long>
) : SplitInput