package com.example.communitypolls.ui.polls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitypolls.data.poll.PollRepository
import com.example.communitypolls.model.Poll
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PollListUiState(
    val loading: Boolean = true,
    val items: List<Poll> = emptyList(),
    val error: String? = null
)

/**
 * Streams active polls for the home feed.
 * We'll provide a factory later to inject PollRepository from ServiceLocator.
 */
class PollListViewModel(
    private val repo: PollRepository,
    private val limit: Int = 50
) : ViewModel() {

    private val _state = MutableStateFlow(PollListUiState())
    val state: StateFlow<PollListUiState> = _state.asStateFlow()

    private var streamJob: Job? = null

    init {
        startStream()
    }

    fun refresh() {
        // Re-start the stream; useful after transient errors
        startStream()
    }

    private fun startStream() {
        streamJob?.cancel()
        _state.value = _state.value.copy(loading = true, error = null)

        streamJob = viewModelScope.launch {
            try {
                repo.observeActivePolls(limit).collect { list ->
                    _state.value = PollListUiState(
                        loading = false,
                        items = list,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Failed to load polls"
                )
            }
        }
    }
}
