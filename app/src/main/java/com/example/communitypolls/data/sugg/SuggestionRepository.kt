package com.example.communitypolls.data.sugg

import com.example.communitypolls.model.Suggestion
import kotlinx.coroutines.flow.Flow

sealed interface SuggestResult {
    data class Success(val id: String) : SuggestResult
    data class Error(val message: String) : SuggestResult
}

sealed interface SuggOp {
    data object Success : SuggOp
    data class Error(val message: String) : SuggOp
}

interface SuggestionRepository {
    suspend fun createSuggestion(
        title: String,
        description: String,
        createdByUid: String,
        createdByName: String
    ): SuggestResult

    fun observeAllSuggestions(): Flow<List<Suggestion>>
    suspend fun updateStatus(id: String, newStatus: String): SuggOp
    suspend fun delete(id: String): SuggOp
}
