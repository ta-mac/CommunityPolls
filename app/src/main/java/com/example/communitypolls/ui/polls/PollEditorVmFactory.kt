package com.example.communitypolls.ui.polls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.communitypolls.ui.ServiceLocator

class PollEditorVmFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = ServiceLocator.pollRepository
        return PollEditorViewModel(repo) as T
    }
}
