package com.example.communitypolls.ui.polls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitypolls.data.poll.OpResult
import com.example.communitypolls.data.poll.PollRepository
import com.example.communitypolls.model.Poll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PollEditViewModel(
    private val repo: PollRepository,
    private val pollId: String
) : ViewModel() {

    private val _state = MutableStateFlow(
        PollEditorUiState(loading = true) // reuse same UI state as create
    )
    val state: StateFlow<PollEditorUiState> = _state.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observePoll(pollId).collectLatest { p ->
                if (p == null) {
                    _state.value = _state.value.copy(loading = false, error = "Poll not found")
                } else {
                    _state.value = _state.value.copy(
                        loading = false,
                        title = p.title,
                        description = p.description,
                        options = p.options.map { PollEditorOption(id = it.id, text = it.text) },
                        isActive = p.isActive,
                        closesAt = p.closesAt,
                        error = null
                    )
                }
            }
        }
    }

    fun setTitle(value: String) { _state.value = _state.value.copy(title = value, error = null) }
    fun setDescription(value: String) { _state.value = _state.value.copy(description = value, error = null) }
    fun setOptionId(index: Int, value: String) = updateOption(index) { it.copy(id = value) }
    fun setOptionText(index: Int, value: String) = updateOption(index) { it.copy(text = value) }
    fun addOption() { _state.value = _state.value.copy(options = _state.value.options + PollEditorOption(), error = null) }
    fun removeOption(index: Int) {
        val list = _state.value.options.toMutableList()
        if (index in list.indices && list.size > 2) {
            list.removeAt(index)
            _state.value = _state.value.copy(options = list, error = null)
        } else {
            _state.value = _state.value.copy(error = "A poll must have at least two options.")
        }
    }
    fun setActive(active: Boolean) { _state.value = _state.value.copy(isActive = active, error = null) }
    fun setCloseAfterHours(hours: Long?) {
        val closes = if (hours == null) null else System.currentTimeMillis() + hours * 60L * 60L * 1000L
        _state.value = _state.value.copy(closesAt = closes, error = null)
    }
    fun clearError() { _state.value = _state.value.copy(error = null) }

    fun save() {
        val s = _state.value
        val title = s.title.trim()
        if (title.isEmpty()) {
            _state.value = s.copy(error = "Title is required.")
            return
        }
        val cleaned = s.options.map { PollEditorOption(it.id.trim(), it.text.trim()) }
        if (cleaned.size < 2 || cleaned.any { it.id.isEmpty() || it.text.isEmpty() }) {
            _state.value = s.copy(error = "Please provide at least 2 options with id and text.")
            return
        }

        _state.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val res = repo.updatePoll(
                pollId = pollId,
                title = title,
                description = s.description.trim(),
                options = cleaned.map { com.example.communitypolls.model.PollOption(id = it.id, text = it.text) },
                closesAtMillis = s.closesAt,
                isActive = s.isActive
            )) {
                is OpResult.Success -> {
                    _state.value = _state.value.copy(loading = false)
                    _saved.value = true
                }
                is OpResult.Error -> {
                    _state.value = _state.value.copy(loading = false, error = res.message)
                }
            }
        }
    }

    // --- helper ---
    private fun updateOption(index: Int, transform: (PollEditorOption) -> PollEditorOption) {
        val list = _state.value.options.toMutableList()
        if (index in list.indices) {
            list[index] = transform(list[index])
            _state.value = _state.value.copy(options = list, error = null)
        }
    }
}
