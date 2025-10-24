package com.example.communitypolls.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import com.example.communitypolls.model.Poll
import com.example.communitypolls.ui.polls.PollListScreen
import com.example.communitypolls.ui.polls.PollListUiState
import com.example.communitypolls.ui.polls.PollSort
import org.junit.Rule
import org.junit.Test

class PollListScreenTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun loadingState_showsMessage() {
        rule.setContent {
            PollListScreen(
                state = PollListUiState(loading = true),
                onRetry = {},
                onPollClick = {},
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = PollSort.NEWEST,
                onCreatePoll = {}
            )
        }

        rule.onNodeWithText("Loading polls…").assertIsDisplayed()
    }

    @Test
    fun errorState_rendersAndRefreshCallsOnRetry() {
        var retried = false

        rule.setContent {
            PollListScreen(
                state = PollListUiState(loading = false, error = "Boom"),
                onRetry = { retried = true },
                onPollClick = {},
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = PollSort.NEWEST,
                onCreatePoll = {}
            )
        }

        rule.onNodeWithText("Boom").assertIsDisplayed()
        rule.onNodeWithText("Refresh").performClick()
        assert(retried)
    }

    @Test
    fun list_rendersItems_andOnPollClickFires() {
        var clickedId: String? = null
        val items = listOf(
            Poll(id = "p1", title = "Favorite fruit", description = "Pick one", createdAt = 0L, isActive = true, options = emptyList(), createdBy = "", closesAt = null),
            Poll(id = "p2", title = "Best OS", description = "Vote!", createdAt = 0L, isActive = true, options = emptyList(), createdBy = "", closesAt = null)
        )

        rule.setContent {
            PollListScreen(
                state = PollListUiState(loading = false, items = items),
                onRetry = {},
                onPollClick = { clickedId = it },
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = PollSort.NEWEST,
                onCreatePoll = {}
            )
        }

        rule.onNodeWithText("Favorite fruit").assertIsDisplayed()
        rule.onNodeWithText("Best OS").assertIsDisplayed()

        rule.onNodeWithText("Favorite fruit").performClick()
        assert(clickedId == "p1")
    }

    @Test
    fun adminActions_visible_whenEnabled_andCallbacksFire() {
        var edited: String? = null
        var deleted: String? = null
        val items = listOf(
            Poll(id = "p1", title = "Editable poll", description = "", createdAt = 0L, isActive = true, options = emptyList(), createdBy = "", closesAt = null)
        )

        rule.setContent {
            PollListScreen(
                state = PollListUiState(loading = false, items = items),
                onRetry = {},
                onPollClick = {},
                showAdminActions = true,
                onEditPoll = { edited = it },
                onDeletePoll = { deleted = it },
                sort = PollSort.NEWEST,
                onCreatePoll = {}
            )
        }

        rule.onNodeWithText("Edit").assertIsDisplayed().performClick()
        rule.onNodeWithText("Delete").assertIsDisplayed().performClick()

        assert(edited == "p1")
        assert(deleted == "p1")
    }
}
