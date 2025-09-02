package com.example.communitypolls.ui.polls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitypolls.data.poll.OpResult
import com.example.communitypolls.data.poll.PollRepository
import com.example.communitypolls.model.Poll
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class VoteUiState(
    val loading: Boolean = true,
    val poll: Poll? = null,
    val selectedOptionId: String? = null,
    val submitting: Boolean = false,
    val error: String? = null,
    val submitted: Boolean = false
)

class PollVoteViewModel(
    private val repo: PollRepository,
    private val pollId: String
) : ViewModel() {

    private val _ui = MutableStateFlow(VoteUiState())
    val ui: StateFlow<VoteUiState> = _ui

    init {
        viewModelScope.launch {
            repo.observePoll(pollId).collectLatest { p ->
                _ui.value = _ui.value.copy(loading = false, poll = p, error = null)
            }
        }
    }

    fun select(optionId: String) {
        _ui.value = _ui.value.copy(selectedOptionId = optionId)
    }

    fun submit() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val option = _ui.value.selectedOptionId
        val p = _ui.value.poll
        if (uid == null) {
            _ui.value = _ui.value.copy(error = "Please sign in to vote.")
            return
        }
        if (p == null || option == null) {
            _ui.value = _ui.value.copy(error = "Select an option.")
            return
        }
        if (!p.isActive) {
            _ui.value = _ui.value.copy(error = "This poll is closed.")
            return
        }

        _ui.value = _ui.value.copy(submitting = true, error = null)
        viewModelScope.launch {
            when (val res = repo.castVote(pollId = p.id, optionId = option, voterUid = uid)) {
                is OpResult.Success -> _ui.value = _ui.value.copy(submitting = false, submitted = true)
                is OpResult.Error -> _ui.value = _ui.value.copy(submitting = false, error = res.message)
            }
        }
    }

    fun clearError() { _ui.value = _ui.value.copy(error = null) }
}
