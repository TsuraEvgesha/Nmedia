package ru.netology.nmedia.dto



sealed interface FeedItem {
    val id: Long

}
data class Post(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    var attachment: Attachment? = null,
    var savedOnServer: Boolean,
    var ownedByMe: Boolean=false



    ):FeedItem {
    val view: Long = 0
    val video: String? = null
    val share: Long=0
    val shareReal: Boolean=false
}
data class Ad(
    override val id: Long,
    val image: String
): FeedItem

data class Attachment(
    val url: String,
    val type: AttachmentType= AttachmentType.IMAGE,
)

enum class AttachmentType {
    IMAGE
}