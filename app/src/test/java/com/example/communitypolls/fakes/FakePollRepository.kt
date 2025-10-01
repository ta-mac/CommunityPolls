package com.example.communitypolls.fakes

import com.example.communitypolls.data.poll.CreatePollResult
import com.example.communitypolls.data.poll.OpResult
import com.example.communitypolls.data.poll.PollRepository
import com.example.communitypolls.model.Poll
import com.example.communitypolls.model.PollOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * In-memory implementation of PollRepository for fast, deterministic unit tests.
 * This does NOT depend on Firebase and can be used in local JVM tests.
 */
class FakePollRepository : PollRepository {

    private val polls = ConcurrentHashMap<String, Poll>()
    private val votes = ConcurrentHashMap<String, MutableStateFlow<Map<String, String>>>()
    private val pollList = MutableStateFlow<List<Poll>>(emptyList())
    private val idCounter = AtomicInteger(1)

    override fun observeActivePolls(limit: Int?): Flow<List<Poll>> =
        pollList.map { list ->
            val active = list.filter { it.isActive }
            if (limit != null) active.take(limit) else active
        }

    override fun observePoll(pollId: String): Flow<Poll?> =
        pollList.map { list -> list.find { it.id == pollId } }

    override fun observeResults(pollId: String): Flow<Map<String, Int>> =
        votes.getOrPut(pollId) { MutableStateFlow(emptyMap()) }.map { uidToChoice ->
            uidToChoice.values.groupingBy { it }.eachCount()
        }

    override suspend fun createPoll(
        title: String,
        description: String,
        options: List<PollOption>,
        createdByUid: String,
        closesAtMillis: Long?,
        isActive: Boolean
    ): CreatePollResult {
        val trimmedTitle = title.trim()
        val cleanOptions = options.map { it.copy(id = it.id.trim(), text = it.text.trim()) }
        if (trimmedTitle.isBlank()) return CreatePollResult.Error("Title cannot be blank")
        if (cleanOptions.size < 2) return CreatePollResult.Error("At least two options are required")
        if (cleanOptions.any { it.text.isBlank() }) return CreatePollResult.Error("Option text cannot be blank")
        if (cleanOptions.map { it.id }.toSet().size != cleanOptions.size) return CreatePollResult.Error("Option IDs must be unique")

        val newId = "poll_${idCounter.getAndIncrement()}"
        val now = System.currentTimeMillis()
        val poll = Poll(
            id = newId,
            title = trimmedTitle,
            description = description,
            options = cleanOptions,
            createdBy = createdByUid,
            createdAt = now,
            closesAt = closesAtMillis,
            isActive = isActive
        )
        polls[newId] = poll
        votes.putIfAbsent(newId, MutableStateFlow(emptyMap()))
        pollList.update { polls.values.sortedByDescending { it.createdAt } }
        return CreatePollResult.Success(newId)
    }

    override suspend fun updatePoll(
        pollId: String,
        title: String?,
        description: String?,
        options: List<PollOption>?,
        closesAtMillis: Long?,
        isActive: Boolean?
    ): OpResult {
        val existing = polls[pollId] ?: return OpResult.Error("Poll not found")
        val updated = existing.copy(
            title = title?.takeIf { it.isNotBlank() } ?: existing.title,
            description = description ?: existing.description,
            options = options ?: existing.options,
            closesAt = closesAtMillis ?: existing.closesAt,
            isActive = isActive ?: existing.isActive
        )
        polls[pollId] = updated
        pollList.update { polls.values.sortedByDescending { it.createdAt } }
        return OpResult.Success
    }

    override suspend fun deletePoll(pollId: String): OpResult {
        val removed = polls.remove(pollId) ?: return OpResult.Error("Poll not found")
        votes.remove(pollId)
        pollList.update { polls.values.sortedByDescending { it.createdAt } }
        return OpResult.Success
    }

    override suspend fun castVote(pollId: String, optionId: String, voterUid: String): OpResult {
        val poll = polls[pollId] ?: return OpResult.Error("Poll not found")
        if (poll.options.none { it.id == optionId }) return OpResult.Error("Invalid option")
        val flow = votes.getOrPut(pollId) { MutableStateFlow(emptyMap()) }
        flow.update { it + (voterUid to optionId) }
        return OpResult.Success
    }

    suspend fun currentCounts(pollId: String): Map<String, Int> =
        observeResults(pollId).first()
}
