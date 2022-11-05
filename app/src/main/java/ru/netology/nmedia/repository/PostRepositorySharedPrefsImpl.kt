package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositorySharedPrefsImpl (
    context: Context
) : PostRepository {
    private val gson = Gson()
    private val prefs= context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "posts"
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)
    init {
        prefs.getString(key,null)?.let {
            posts = gson.fromJson(it,type)
            data.value = posts
        }
    }
    override fun getAll():LiveData<List<Post>> = data
    override fun likeById(id:Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(liked = !it.liked,
                likes = if(it.liked) it.likes -1 else it.likes +1)
        }
        data.value=posts
        sync()
    }

    override fun removeById(id: Long) {
        posts=posts.filter { it.id != id }
        data.value=posts
        sync()
    }

    override fun shareById(id:Long) {
        posts = posts.map {
            if(it.id != id) it else it.copy(share = (it.share+1))
        }
        data.value=posts
        sync()
    }

    override fun save(post: Post) {
        if (post.id ==0L){
            val newId = (posts.firstOrNull()?.id?:0L)+1
            posts = listOf(
                post.copy(
                    id= newId,
                    author = "Me",
                    liked = false,
                    published = ""
                )
            )+ posts
            data.value=posts
            return
        }
        posts = posts.map{
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value=posts
        sync()



    }
    private fun sync(){
        with(prefs.edit()){
            putString(key, gson.toJson(posts))
            apply()
        }
    }
}