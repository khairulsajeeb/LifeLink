package com.bmu.lifelink.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmu.lifelink.data.models.BlogPost
import com.bmu.lifelink.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    val blogPosts: StateFlow<List<BlogPost>> = repository.getBlogPostsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BlogPost.starterPosts
        )
}
