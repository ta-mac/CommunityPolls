package com.example.communitypolls.data.sugg

import com.example.communitypolls.model.Suggestion
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseSuggRepository(private val db: FirebaseFirestore) : SuggestionRepository {
    private val col get() = db.collection("suggestions")

    override suspend fun createSuggestion(
        title: String, description: String, createdByUid: String, createdByName: String
    ): SuggestResult {
        val t = title.trim(); val d = description.trim(); val n = createdByName.trim()
        if (t.isBlank()) return SuggestResult.Error("Title is required")
        if (d.isBlank()) return SuggestResult.Error("Description is required")
        if (n.isBlank()) return SuggestResult.Error("Your name or email is required")

        return try {
            val ref = col.add(
                mapOf(
                    "title" to t, "description" to d,
                    "createdBy" to createdByUid, "createdByName" to n,
                    "createdAt" to System.currentTimeMillis(), "status" to "pending"
                )
            ).await()
            SuggestResult.Success(ref.id)
        } catch (e: Exception) {
            SuggestResult.Error(e.message ?: "Failed to submit suggestion")
        }
    }

    override fun observeAllSuggestions(): Flow<List<Suggestion>> = callbackFlow {
        val reg = col.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(emptyList()); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { d ->
                    val obj = d.toObject(Suggestion::class.java) ?: return@mapNotNull null
                    obj.copy(id = d.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    override suspend fun updateStatus(id: String, newStatus: String): SuggOp {
        if (newStatus !in listOf("accepted", "declined")) return SuggOp.Error("Invalid status")
        return try {
            val doc = col.document(id)
            db.runTransaction { tx ->
                val snap = tx.get(doc)
                val current = snap.getString("status") ?: "pending"
                if (current != "pending") error("Only pending suggestions can be changed")
                tx.update(doc, mapOf("status" to newStatus))
            }.await()
            SuggOp.Success
        } catch (e: Exception) {
            SuggOp.Error(e.message ?: "Failed to update status")
        }
    }
<<<<<<< HEAD

    override suspend fun delete(id: String): SuggOp {
        return try {
            col.document(id).delete().await()
            SuggOp.Success
        } catch (e: Exception) {
            SuggOp.Error(e.message ?: "Failed to delete suggestion")
        }
    }


=======
>>>>>>> 0af30b8 (Added some security measures)
}
