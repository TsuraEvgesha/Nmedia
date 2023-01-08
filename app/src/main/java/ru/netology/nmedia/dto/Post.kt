package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String = "",
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    var see: Boolean,
    var attachment: Attachment? = null,



    ) {
    val view: Long = 0
    val video: String? = null
    val share: Long=0
    val shareReal: Boolean=false
}
data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)

enum class AttachmentType {
    IMAGE
}