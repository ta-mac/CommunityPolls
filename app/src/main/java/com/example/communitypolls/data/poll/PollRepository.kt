package com.example.communitypolls.data.poll

import com.example.communitypolls.model.Poll
import com.example.communitypolls.model.PollOption
import kotlinx.coroutines.flow.Flow

/** Returned by create() so callers get the new pollId or an error. */
sealed interface CreatePollResult {
    data class Success(val pollId: String) : CreatePollResult
    data class Error(val message: String) : CreatePollResult
}

/** Generic op result for update/delete/vote. */
sealed interface OpResult {
    data object Success : OpResult
    data class Error(val message: String) : OpResult
}

/**
 * Abstraction for polls data.
 */
interface PollRepository {

    /** Stream active polls (used on home screens). */
    fun observeActivePolls(limit: Int? = null): Flow<List<Poll>>

    /** Stream a single poll (for the vote or results screen). */
    fun observePoll(pollId: String): Flow<Poll?>

    /** Live results: map of optionId -> vote count. */
    fun observeResults(pollId: String): Flow<Map<String, Int>>

    /** Create a poll (admin only via rules). */
    suspend fun createPoll(
        title: String,
        description: String,
        options: List<PollOption>,
        createdByUid: String,
        closesAtMillis: Long? = null,
        isActive: Boolean = true
    ): CreatePollResult

    /**
     * Update fields of a poll by id. Pass only the fields you need to change.
     * If [options] is provided it replaces the existing option list in full.
     */
    suspend fun updatePoll(
        pollId: String,
        title: String? = null,
        description: String? = null,
        options: List<PollOption>? = null,
        closesAtMillis: Long? = null,
        isActive: Boolean? = null
    ): OpResult

    /** Delete a poll (admin-only via security rules). */
    suspend fun deletePoll(pollId: String): OpResult

    /**
     * Cast a vote to /polls/{pollId}/votes/{uid}.
     * @param anonymous whether to mark this vote as anonymous in Firestore.
     */
    suspend fun castVote(
        pollId: String,
        optionId: String,
        voterUid: String,
        anonymous: Boolean = false
    ): OpResult
}
