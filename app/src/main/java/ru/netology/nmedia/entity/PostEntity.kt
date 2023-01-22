package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId:Long,
    val author: String,
    val authorAvatar:String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val see: Boolean,
    val savedOnServer: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?,

){
    fun toDto() = Post(id, authorId, author, authorAvatar, content, published, likedByMe, likes,see, attachment?.toDto(), savedOnServer)
    companion object{
        fun fromDto(dto: Post) = PostEntity(dto.id,dto.authorId,dto.author,dto.authorAvatar,dto.content,dto.published,dto.likedByMe,dto.likes,dto.see,dto.savedOnServer, AttachmentEmbeddable.fromDto(dto.attachment))

    }

}
data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
){
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }


}
fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)




