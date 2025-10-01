package com.example.communitypolls.model

/**
 * Basic user profile used by the app UI.
 * role: "user" | "admin" | "guest"
 */
data class AppUser(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "user"
)
