package com.example.communitypolls.ui.sugg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitypolls.data.sugg.SuggestResult
import com.example.communitypolls.data.sugg.SuggestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SuggestPollState(
    val loading: Boolean = false,
    val error: String? = null,
    val title: String = "",
    val description: String = "",
    val yourNameOrEmail: String = "",
    val submittedId: String? = null
)

class SuggestPollViewModel(
    private val repo: SuggestionRepository,
    private val createdByUid: String,
    initialNameOrEmail: String

) : ViewModel() {

    private val _state = MutableStateFlow(SuggestPollState(yourNameOrEmail = initialNameOrEmail))
    val state = _state.asStateFlow()

    fun setTitle(v: String)       = _state.update { it.copy(title = v, error = null) }
    fun setDescription(v: String) = _state.update { it.copy(description = v, error = null) }
    fun setNameOrEmail(v: String) = _state.update { it.copy(yourNameOrEmail = v, error = null) }
    fun clearError()              = _state.update { it.copy(error = null) }
    fun consumeSubmitted()        = _state.update { it.copy(submittedId = null) }

    fun submit() {
        val s = _state.value
        if (s.loading) return

        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val res = repo.createSuggestion(
                title = s.title,
                description = s.description,
                createdByUid = createdByUid,
                createdByName = s.yourNameOrEmail
            )) {
                is SuggestResult.Success -> _state.update { it.copy(loading = false, submittedId = res.id) }
                is SuggestResult.Error   -> _state.update { it.copy(loading = false, error = res.message) }
            }
        }
    }
}
