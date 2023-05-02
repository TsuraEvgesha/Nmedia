package ru.netology.nmedia.repository


import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<PagingData<FeedItem>>
    suspend fun getAll()
    suspend fun save(post: Post, upload: MediaUpload?)
    suspend fun likeById(id: Long)
//    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    //    fun sharedById(id: Long):Post
    suspend fun dislikeById(id:Long)
    suspend fun removeById(id: Long)
    fun getNewerCount(id: Long): Flow<Int>
//    suspend fun updateStatus()
    suspend fun upload(upload: MediaUpload): Media
    suspend fun updateUser(login: String, pass: String)
    suspend fun registerUser(login: String, pass: String, name: String)
}