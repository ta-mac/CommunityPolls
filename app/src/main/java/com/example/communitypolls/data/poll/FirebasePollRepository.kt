package com.example.communitypolls.data.poll

import android.util.Log
import com.example.communitypolls.model.Poll
import com.example.communitypolls.model.PollOption
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.perf.FirebasePerformance              // NEW: Performance monitoring
import com.google.firebase.perf.metrics.Trace                   // NEW: Performance tracing
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "FirebasePollRepository"

class FirebasePollRepository(
    private val db: FirebaseFirestore
) : PollRepository {

    // 🔥 NEW: In-memory cache to reduce reads
    private val pollCache = mutableMapOf<String, Poll>()

    private val polls get() = db.collection("polls")

    // ---------- Streams ----------

    override fun observeActivePolls(limit: Int?): Flow<List<Poll>> = callbackFlow {
        var reg: ListenerRegistration? = null

        fun listenFallback() {
            var q: Query = polls.whereEqualTo("isActive", true)
            if (limit != null) q = q.limit(limit.toLong())
            reg = q.addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e(TAG, "Fallback query failed: ${err.message}", err)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val list = snap?.documents?.mapNotNull { it.toPoll() } ?: emptyList()

                // 🔥 Cache results locally
                list.forEach { pollCache[it.id] = it }

                trySend(list)
            }
        }

        fun listenOrdered() {
            var q: Query = polls
                .whereEqualTo("isActive", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
            if (limit != null) q = q.limit(limit.toLong())

            reg = q.addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e(TAG, "Ordered query failed: ${err.message}", err)
                    if (err is FirebaseFirestoreException &&
                        err.code == FirebaseFirestoreException.Code.FAILED_PRECONDITION) {
                        // Missing composite index: use fallback so items don't vanish
                        reg?.remove()
                        listenFallback()
                        trySend(emptyList())
                    } else {
                        trySend(emptyList())
                    }
                    return@addSnapshotListener
                }

                val list = snap?.documents?.mapNotNull { it.toPoll() } ?: emptyList()

                // 🔥 Cache results locally
                list.forEach { pollCache[it.id] = it }

                trySend(list)
            }
        }

        // Start ordered (preferred); fall back if index is missing
        listenOrdered()
        awaitClose { reg?.remove() }
    }

    override fun observePoll(pollId: String): Flow<Poll?> = callbackFlow {
        // Serve from cache first if available
        pollCache[pollId]?.let { trySend(it) }

        val reg = polls.document(pollId).addSnapshotListener { snap, err ->
            if (err != null) {
                Log.e(TAG, "observePoll failed: ${err.message}", err)
                trySend(null)
                return@addSnapshotListener
            }
            val poll = snap?.toPoll()
            if (poll != null) pollCache[poll.id] = poll
            trySend(poll)
        }
        awaitClose { reg.remove() }
    }

    override fun observeResults(pollId: String): Flow<Map<String, Int>> = callbackFlow {
        val reg = polls.document(pollId)
            .collection("votes")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e(TAG, "observeResults failed: ${err.message}", err)
                    trySend(emptyMap())
                    return@addSnapshotListener
                }
                val counts = mutableMapOf<String, Int>()
                snap?.documents?.forEach { d ->
                    val optionId = d.getString("optionId") ?: return@forEach
                    counts[optionId] = (counts[optionId] ?: 0) + 1
                }
                trySend(counts)
            }
        awaitClose { reg.remove() }
    }

    // ---------- Mutations ----------

    override suspend fun createPoll(
        title: String,
        description: String,
        options: List<PollOption>,
        createdByUid: String,
        closesAtMillis: Long?,
        isActive: Boolean
    ): CreatePollResult {
        val trace: Trace = FirebasePerformance.getInstance().newTrace("create_poll_trace")
        trace.start()

        val trimmedOptions = options.map { it.copy(id = it.id.trim(), text = it.text.trim()) }
        if (title.isBlank()) return CreatePollResult.Error("Title is required")
        if (trimmedOptions.size < 2) return CreatePollResult.Error("Add at least two options")
        if (trimmedOptions.any { it.id.isBlank() || it.text.isBlank() }) {
            return CreatePollResult.Error("Each option needs an id and text")
        }

        val data = hashMapOf(
            "title" to title.trim(),
            "description" to description.trim(),
            "options" to trimmedOptions.map { mapOf("id" to it.id, "text" to it.text) },
            "createdBy" to createdByUid,
            "createdAt" to System.currentTimeMillis(),
            "closesAt" to closesAtMillis,
            "isActive" to isActive
        )

        return try {
            val doc = polls.document()
            doc.set(data).await()

            val newPoll = Poll(doc.id, title, description, trimmedOptions, createdByUid, System.currentTimeMillis(), closesAtMillis, isActive)
            pollCache[newPoll.id] = newPoll

            trace.stop()
            CreatePollResult.Success(doc.id)
        } catch (e: Exception) {
            trace.stop()
            CreatePollResult.Error(e.message ?: "Failed to create poll")
        }
    }

    override suspend fun updatePoll(
        pollId: String,
        title: String?,
        description: String?,
        options: List<PollOption>?,
        closesAtMillis: Long?,
        isActive: Boolean?
    ): OpResult {
        val trace = FirebasePerformance.getInstance().newTrace("update_poll_trace")
        trace.start()

        val updates = HashMap<String, Any?>()
        title?.let { updates["title"] = it }
        description?.let { updates["description"] = it }
        options?.let {
            if (it.size < 2) return OpResult.Error("At least two options required")
            val cleaned = it.map { o ->
                val id = o.id.trim()
                val text = o.text.trim()
                if (id.isBlank() || text.isBlank()) return OpResult.Error("Option id/text required")
                mapOf("id" to id, "text" to text)
            }
            updates["options"] = cleaned
        }
        if (closesAtMillis != null) updates["closesAt"] = closesAtMillis
        if (isActive != null) updates["isActive"] = isActive

        return try {
            polls.document(pollId).update(updates as Map<String, Any>).await()
            pollCache.remove(pollId) // Invalidate cache
            trace.stop()
            OpResult.Success
        } catch (e: Exception) {
            trace.stop()
            OpResult.Error(e.message ?: "Failed to update poll")
        }
    }

    override suspend fun deletePoll(pollId: String): OpResult {
        val trace = FirebasePerformance.getInstance().newTrace("delete_poll_trace")
        trace.start()
        return try {
            polls.document(pollId).delete().await()
            pollCache.remove(pollId)
            trace.stop()
            OpResult.Success
        } catch (e: Exception) {
            trace.stop()
            OpResult.Error(e.message ?: "Failed to delete poll")
        }
    }

    override suspend fun castVote(
        pollId: String,
        optionId: String,
        voterUid: String
    ): OpResult {
        val trace = FirebasePerformance.getInstance().newTrace("cast_vote_trace")
        trace.start()
        return try {
            val vote = mapOf(
                "optionId" to optionId,
                "createdAt" to System.currentTimeMillis()
            )
            polls.document(pollId)
                .collection("votes")
                .document(voterUid)
                .set(vote)
                .await()
            trace.stop()
            OpResult.Success
        } catch (e: Exception) {
            trace.stop()
            OpResult.Error(e.message ?: "Failed to cast vote")
        }
    }

    // ---------- Mapping ----------

    private fun DocumentSnapshot.toPoll(): Poll? {
        if (!exists()) return null
        val data = data ?: return null
        val id = id
        val opts = (data["options"] as? List<*>)?.mapNotNull { raw ->
            val map = raw as? Map<*, *> ?: return@mapNotNull null
            val oid = map["id"] as? String ?: return@mapNotNull null
            val txt = map["text"] as? String ?: ""
            PollOption(id = oid, text = txt)
        } ?: emptyList()

        return Poll(
            id = id,
            title = (data["title"] as? String).orElseEmpty(),
            description = (data["description"] as? String).orElseEmpty(),
            options = opts,
            createdBy = (data["createdBy"] as? String).orElseEmpty(),
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L,
            closesAt = (data["closesAt"] as? Number)?.toLong(),
            isActive = (data["isActive"] as? Boolean) ?: true
        )
    }

    private fun String?.orElseEmpty(): String = this ?: ""
}
