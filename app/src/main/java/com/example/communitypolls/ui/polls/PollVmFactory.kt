package com.example.communitypolls.ui.polls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.communitypolls.ui.ServiceLocator


class PollVmFactory(
    private val limit: Int = 50
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Add more ViewModels here later if needed.
        if (modelClass.isAssignableFrom(PollListViewModel::class.java)) {
            val repo = ServiceLocator.pollRepository
            return PollListViewModel(repo, limit) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
