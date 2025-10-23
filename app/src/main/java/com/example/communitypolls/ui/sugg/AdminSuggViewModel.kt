package com.example.communitypolls.ui.sugg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitypolls.data.sugg.SuggOp
import com.example.communitypolls.data.sugg.SuggestionRepository
import com.example.communitypolls.model.Suggestion
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdminSuggState(
    val loading: Boolean = true,
    val items: List<Suggestion> = emptyList(),
    val error: String? = null
)

class AdminSuggViewModel(
    private val repo: SuggestionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminSuggState())
    val state: StateFlow<AdminSuggState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observeAllSuggestions()
                .onEach { _state.value = _state.value.copy(loading = false, items = it, error = null) }
                .catch { e -> _state.value = _state.value.copy(loading = false, error = e.message ?: "Failed to load") }
                .collect()
        }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }

    fun setStatus(id: String, status: String) {
        viewModelScope.launch {
            when (repo.updateStatus(id, status)) {
                is SuggOp.Success -> Unit
                is SuggOp.Error   -> _state.value = _state.value.copy(error = "Could not set $status")
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            when (repo.delete(id)) {
                is SuggOp.Success -> {
                    // Let firestore observer auto-update the list
                }
                is SuggOp.Error -> {
                    _state.value = _state.value.copy(error = "Could not delete suggestion")
                }
            }
        }
    }

}
