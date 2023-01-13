package ru.netology.nmedia.adapter

import ru.netology.nmedia.dto.Post


interface PostListener  {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemote(post: Post)
    fun onEdit(post: Post)
    fun onPlayVideo(post: Post)
    fun onPost(post: Post)
    fun onMedia(post: Post)

}