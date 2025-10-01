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
import java.util.Locale

data class PollEditorOption(val id: String = "", val text: String = "")
data class PollEditorState(
    val title: String = "",
    val description: String = "",
    val options: List<PollEditorOption> = listOf(PollEditorOption(), PollEditorOption()),
    val isActive: Boolean = true,
    val closeAfterHours: Int? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val savedId: String? = null,
)

class PollEditorViewModel(private val repo: PollRepository) : ViewModel() {
    private val _state = MutableStateFlow(PollEditorState())
    val state: StateFlow<PollEditorState> = _state.asStateFlow()

    fun setTitle(v: String) { _state.value = _state.value.copy(title = v, error = null) }
    fun setDescription(v: String) { _state.value = _state.value.copy(description = v, error = null) }
    fun setOptionId(i: Int, v: String) = updateOption(i) { it.copy(id = v) }
    fun setOptionText(i: Int, v: String) = updateOption(i) { it.copy(text = v) }
    fun addOption() { _state.value = _state.value.copy(options = _state.value.options + PollEditorOption()) }
    fun removeOption(i: Int) {
        val list = _state.value.options.toMutableList()
        if (i in list.indices && list.size > 2) { list.removeAt(i); _state.value = _state.value.copy(options = list) }
    }
    fun setActive(v: Boolean) { _state.value = _state.value.copy(isActive = v) }
    fun setCloseAfterHours(v: Int?) { _state.value = _state.value.copy(closeAfterHours = v) }
    fun clearError() { _state.value = _state.value.copy(error = null) }

    fun save(createdByUid: String) {
        val s = _state.value
        if (s.loading) return

        val norm = s.options.map { it.copy(id = it.id.trim(), text = it.text.trim()) }
        if (s.title.isBlank()) { _state.value = s.copy(error = "Title is required"); return }
        if (norm.size < 2) { _state.value = s.copy(error = "Add at least two options"); return }
        val withIds = norm.map { if (it.id.isNotBlank()) it else it.copy(id = slug(it.text)) }
        if (withIds.any { it.id.isBlank() || it.text.isBlank() }) {
            _state.value = s.copy(error = "Each option needs both an ID and Text"); return
        }
        val dedup = ensureUniqueIds(withIds.map { it.id })
        val repoOptions = withIds.mapIndexed { idx, o -> PollOption(id = dedup[idx], text = o.text) }
        val closesAtMillis = s.closeAfterHours?.let { h -> System.currentTimeMillis() + h * 60L * 60L * 1000L }

        _state.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val res = repo.createPoll(
                title = s.title.trim(),
                description = s.description.trim(),
                options = repoOptions,
                createdByUid = createdByUid,
                closesAtMillis = closesAtMillis,
                isActive = s.isActive
            )) {
                is CreatePollResult.Success -> _state.value = _state.value.copy(loading = false, savedId = res.pollId)
                is CreatePollResult.Error   -> _state.value = _state.value.copy(loading = false, error = res.message)
            }
        }
    }

    private fun updateOption(i: Int, f: (PollEditorOption) -> PollEditorOption) {
        val list = _state.value.options.toMutableList()
        if (i in list.indices) { list[i] = f(list[i]); _state.value = _state.value.copy(options = list, error = null) }
    }
    private fun slug(t: String) = t.lowercase(Locale.ROOT).replace("[^a-z0-9]+".toRegex(), "_").trim('_')
    private fun ensureUniqueIds(ids: List<String>): List<String> {
        val used = mutableSetOf<String>()
        return ids.map { base ->
            var c = base; var i = 2
            while (c in used || c.isBlank()) c = "${base}_${i++}"
            used.add(c); c
        }
    }
}
