package com.example.communitypolls.model

data class Suggestion(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val createdBy: String = "",
    val createdByName: String = "",
    val createdAt: Long = 0L,
    val status: String = "pending" // "pending" | "accepted" | "declined"
)
