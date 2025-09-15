package com.example.communitypolls

import app.cash.turbine.test
import com.example.communitypolls.data.poll.CreatePollResult
import com.example.communitypolls.data.poll.OpResult
import com.example.communitypolls.data.poll.PollRepository
import com.example.communitypolls.fakes.FakePollRepository
import com.example.communitypolls.model.PollOption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PollRepositoryContractTest {

    private fun makeRepo(): PollRepository = FakePollRepository()

    private suspend fun seed(repo: PollRepository): String {
        val res = repo.createPoll(
            title = "Best fruit?",
            description = "Pick one",
            options = listOf(PollOption("apple","Apple"), PollOption("banana","Banana")),
            createdByUid = "admin1",
            closesAtMillis = null,
            isActive = true
        )
        assertTrue(res is CreatePollResult.Success)
        return (res as CreatePollResult.Success).pollId
    }

    @Test fun create_and_observe_poll() = runTest {
        val repo = makeRepo()
        val id = seed(repo)

        repo.observePoll(id).test {
            val first = awaitItem()
            assertNotNull(first)
            assertEquals("Best fruit?", first!!.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test fun active_list_filters_and_limit() = runTest {
        val repo = makeRepo()
        val id1 = seed(repo)
        val res2 = repo.createPoll(
            title = "Second",
            description = "",
            options = listOf(PollOption("x","X"), PollOption("y","Y")),
            createdByUid = "admin1",
            closesAtMillis = null,
            isActive = false
        )
        assertTrue(res2 is CreatePollResult.Success)

        repo.observeActivePolls().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals(id1, list[0].id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test fun update_poll_changes_streams() = runTest {
        val repo = makeRepo()
        val id = seed(repo)

        assertTrue(repo.updatePoll(id, title = "Better fruit?", description = null, options = null, closesAtMillis = null, isActive = null) is OpResult.Success)

        repo.observePoll(id).test {
            val p = awaitItem()
            assertEquals("Better fruit?", p!!.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test fun delete_poll_removes_everything() = runTest {
        val repo = makeRepo()
        val id = seed(repo)
        assertTrue(repo.deletePoll(id) is OpResult.Success)

        repo.observePoll(id).test {
            val p = awaitItem()
            assertNull(p)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test fun voting_counts_and_overwrites_by_uid() = runTest {
        val repo = makeRepo()
        val id = seed(repo)

        assertTrue(repo.castVote(id, optionId = "apple", voterUid = "u1") is OpResult.Success)

        repo.observeResults(id).test {
            val m1 = awaitItem()
            assertEquals(1, m1["apple"] ?: 0)

            assertTrue(repo.castVote(id, optionId = "banana", voterUid = "u1") is OpResult.Success)
            val m2 = awaitItem()
            assertEquals(1, m2["banana"] ?: 0)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
