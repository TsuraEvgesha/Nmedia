package ru.netology.nmedia.repository


import ru.netology.nmedia.dto.Post

interface PostRepository {
//    fun getAll(): List<Post>
    fun save(post: Post,callback: Callback<Post>)
    fun likeById(id: Long, callback: Callback<Post>)
//    fun sharedById(id: Long):Post
    fun dislikeById(id:Long, callback: Callback<Post>)
    fun removeById(id: Long,callback: Callback<Post>)
    fun getAllAsync(callback:Callback<List<Post>>)
    interface Callback<T> {
        fun onSuccess(posts: T){}
        fun onError(e:Exception, code:Int){}
    }
    
}