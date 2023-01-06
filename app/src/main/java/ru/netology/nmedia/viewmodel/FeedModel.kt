package ru.netology.nmedia.viewmodel




import ru.netology.nmedia.dto.Post

data class FeedModel (
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)
data class FeedModelState(
    val idle: Boolean = false,
    val loading: Boolean=false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)