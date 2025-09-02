package com.example.communitypolls.ui.polls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitypolls.data.poll.CreatePollResult
import com.example.communitypolls.data.poll.PollRepository
import com.example.communitypolls.model.PollOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PollEditorOption(
    val id: String = "",
    val text: String = ""
)

data class PollEditorState(
    val title: String = "",
    val description: String = "",
    val options: List<PollEditorOption> = listOf(
        PollEditorOption(id = "opt1", text = ""),
        PollEditorOption(id = "opt2", text = "")
    ),
    val loading: Boolean = false,
    val error: String? = null,
    val createdPollId: String? = null
)

class PollEditorViewModel(
    private val repo: PollRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PollEditorState())
    val state: StateFlow<PollEditorState> = _state.asStateFlow()

    fun setTitle(value: String) {
        _state.value = _state.value.copy(title = value, error = null)
    }

    fun setDescription(value: String) {
        _state.value = _state.value.copy(description = value, error = null)
    }

    fun setOptionId(index: Int, value: String) {
        updateOption(index) { current -> current.copy(id = value) }
    }

    fun setOptionText(index: Int, value: String) {
        updateOption(index) { current -> current.copy(text = value) }
    }

    fun addOption() {
        val nextIndex = _state.value.options.size + 1
        val newOption = PollEditorOption(id = "opt$nextIndex", text = "")
        _state.value = _state.value.copy(
            options = _state.value.options + newOption,
            error = null
        )
    }

    fun removeOption(index: Int) {
        val list = _state.value.options.toMutableList()
        if (index in list.indices && list.size > 2) { // keep at least 2
            list.removeAt(index)
            _state.value = _state.value.copy(options = list, error = null)
        } else {
            _state.value = _state.value.copy(error = "A poll must have at least two options.")
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    /**
     * Save the poll. createdByUid comes from the signed-in admin (we'll pass it from AppNav).
     * closesAt is omitted for now (null). isActive defaults to true.
     */
    fun save(createdByUid: String) {
        val s = _state.value

        val title = s.title.trim()
        if (title.isEmpty()) {
            _state.value = s.copy(error = "Title is required.")
            return
        }
        val cleaned = s.options.map { it.copy(id = it.id.trim(), text = it.text.trim()) }
        val filtered = cleaned.filter { it.id.isNotEmpty() && it.text.isNotEmpty() }
        if (filtered.size < 2) {
            _state.value = s.copy(error = "Please provide at least two options.")
            return
        }
        val unique = filtered.map { it.id }.toSet().size == filtered.size
        if (!unique) {
            _state.value = s.copy(error = "Option IDs must be unique.")
            return
        }

        _state.value = s.copy(loading = true, error = null)

        viewModelScope.launch {
            val result = repo.createPoll(
                title = title,
                description = s.description.trim(),
                options = filtered.map { PollOption(id = it.id, text = it.text) },
                createdByUid = createdByUid,
                closesAtMillis = null,
                isActive = true
            )
            when (result) {
                is CreatePollResult.Success ->
                    _state.value = _state.value.copy(loading = false, createdPollId = result.pollId)
                is CreatePollResult.Error ->
                    _state.value = _state.value.copy(loading = false, error = result.message)
            }
        }
    }

    // --- helper to update a single option in-place ---
    private fun updateOption(index: Int, transform: (PollEditorOption) -> PollEditorOption) {
        val list = _state.value.options.toMutableList()
        if (index in list.indices) {
            list[index] = transform(list[index])
            _state.value = _state.value.copy(options = list, error = null)
        }
    }
}
