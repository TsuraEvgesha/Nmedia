package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    var see: Boolean,
    var attachment: Attachment? = null,
    var savedOnServer: Boolean = false



    ) {
    val view: Long = 0
    val video: String? = null
    val share: Long=0
    val shareReal: Boolean=false
}
data class Attachment(
    val url: String,
    val type: AttachmentType= AttachmentType.IMAGE,
)

enum class AttachmentType {
    IMAGE
}