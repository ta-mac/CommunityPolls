package com.example.communitypolls.data.sugg

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseSuggestionRepository(
    private val db: FirebaseFirestore
) : SuggestionRepository {

    private val suggestions get() = db.collection("suggestions")

    override suspend fun createSuggestion(
        title: String,
        description: String,
        createdByUid: String,
        createdByName: String
    ): SuggestResult {
        val t = title.trim()
        val d = description.trim()
        val n = createdByName.trim()

        if (t.isBlank()) return SuggestResult.Error("Title is required")
        if (d.isBlank()) return SuggestResult.Error("Description is required")
        if (n.isBlank()) return SuggestResult.Error("Your name or email is required")

        val data = mapOf(
            "title" to t,
            "description" to d,
            "createdBy" to createdByUid,
            "createdByName" to n,
            "createdAt" to System.currentTimeMillis(),
            "status" to "pending"
        )

        return try {
            val ref = suggestions.add(data).await()  // auto-generated doc id
            SuggestResult.Success(ref.id)
        } catch (e: Exception) {
            SuggestResult.Error(e.message ?: "Failed to submit suggestion")
        }
    }
}
