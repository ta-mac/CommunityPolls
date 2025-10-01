package com.example.communitypolls.ui.polls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitypolls.data.poll.PollRepository
import com.example.communitypolls.model.Poll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class OptionResult(
    val optionId: String,
    val label: String,
    val count: Int,
    val pct: Float
)

data class ResultsUiState(
    val loading: Boolean = true,
    val poll: Poll? = null,
    val results: List<OptionResult> = emptyList(),
    val total: Int = 0,
    val error: String? = null
)

class PollResultsViewModel(
    private val repo: PollRepository,
    private val pollId: String
) : ViewModel() {

    private val _ui = MutableStateFlow(ResultsUiState())
    val ui: StateFlow<ResultsUiState> = _ui

    init {
        viewModelScope.launch {
            combine(
                repo.observePoll(pollId),
                repo.observeResults(pollId)
            ) { poll, counts -> poll to counts }
                .collectLatest { (poll, counts) ->
                    if (poll == null) {
                        _ui.value = ResultsUiState(loading = false, poll = null, error = "Poll not found")
                        return@collectLatest
                    }
                    val total = counts.values.sum()
                    val items = poll.options.map { opt ->
                        val c = counts[opt.id] ?: 0
                        val pct = if (total > 0) c.toFloat() / total.toFloat() else 0f
                        OptionResult(opt.id, opt.text, c, pct)
                    }
                    _ui.value = ResultsUiState(loading = false, poll = poll, results = items, total = total)
                }
        }
    }
}
