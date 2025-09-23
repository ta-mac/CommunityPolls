package com.example.communitypolls.data.sugg

sealed interface SuggestResult {
    data class Success(val id: String) : SuggestResult
    data class Error(val message: String) : SuggestResult
}

interface SuggestionRepository {
    /**
     * Creates a new suggestion document with an auto-generated ID.
     * Returns the new Firestore document ID.
     */
    suspend fun createSuggestion(
        title: String,
        description: String,
        createdByUid: String,
        createdByName: String
    ): SuggestResult
}
