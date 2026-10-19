package com.bmu.lifelink.ui.screens.blog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmu.lifelink.data.models.BlogPost
import com.bmu.lifelink.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BlogViewModel(
    repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    val posts: StateFlow<List<BlogPost>> = repository.getBlogPostsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BlogPost.starterPosts
        )

    fun getPostById(id: String): BlogPost? {
        return posts.value.find { it.id == id }
    }
}
