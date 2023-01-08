package ru.netology.nmedia.repository


import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun likeById(id: Long)

    //    fun sharedById(id: Long):Post
    suspend fun dislikeById(id:Long)
    suspend fun removeById(id: Long)
//    suspend fun getAllAsync(callback:Callback<List<Post>>)
//    interface Callback<T> {
//        fun onSuccess(posts: T){}
//        fun onError(e:Exception, code:Int){}
//    }
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun updateStatus()
}