package com.example.communitypolls.model

/**
 * Represents a single user's vote on a poll.
 * IMPORTANT:
 * - When anonymous == true, UI must never display any identifying info (use "Anonymous").
 * - Repository will still write the vote at polls/{pollId}/votes/{uid} to preserve one-vote-per-user.
 */
data class Vote(
    val optionId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val anonymous: Boolean = false,
    val userDisplayName: String? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "optionId" to optionId,
        "timestamp" to timestamp,
        "anonymous" to anonymous,
        "userDisplayName" to userDisplayName
    )
}
