package ru.netology.nmedia.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.counter
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.view.load


const val BASE_URL = "http://10.0.2.2:9999"
internal class PostsAdapter (private val listener: PostListener) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)){
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("unknown item type")
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when(viewType) {
            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, listener)
            }
            R.layout.card_ad -> {
                val binding = CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            else -> error("unknown item type: $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val post = getItem(position) ?: return
//        holder.bind(post)
        when (val item = getItem(position)){
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            null -> error("unknown item type")
        }
    }
}
class AdViewHolder(
    private val binding: CardAdBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(ad: Ad) {
        binding.image.load("$BASE_URL/media/${ad.image}")
    }
}

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = CardPostBinding.inflate(inflater, parent, false)
//        return  PostViewHolder(
//            binding,
//            listener
//        )
//    }
//
//    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
//        val post = getItem(position)?: return
//        holder.bind(post)
//    }


class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: PostListener
    ): RecyclerView.ViewHolder(binding.root){
    fun bind(post: Post){
        getAvatar(post,binding)
        if (post.attachment!=null){
            binding.attachImage.visibility = View.VISIBLE
            getAttachment(post,binding)
        } else binding.attachImage.visibility = View.GONE
        if (!post.savedOnServer){
            binding.like.visibility = View.INVISIBLE
            binding.share.visibility = View.INVISIBLE
        } else {
            binding.like.visibility = View.VISIBLE
            binding.share.visibility = View.VISIBLE
        }
        if (post.savedOnServer){
            binding.savedOnServer.setImageResource(R.drawable.ic_baseline_public_24)
        } else binding.savedOnServer.setImageResource(R.drawable.ic_baseline_public_off_24)


        binding.apply {
            author.text = post.author
            published.text = post.published.toString()
            content.text = post.content
            videoGroup.isVisible = post.video !=null
            share.isChecked = post.shareReal
            like.isChecked = post.likedByMe
            share.text = counter(post.share)
            like.text = counter(post.likes.toLong())
            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            like.setOnClickListener{
                listener.onLike(post)
            }
            share.setOnClickListener {
                listener.onShare(post)
            }
            videoBanner.setOnClickListener {
                listener.onPlayVideo(post)
            }
            playVideo.setOnClickListener {
                listener.onPlayVideo(post)

            }
            videoGroup.setOnClickListener {
                listener.onPlayVideo(post)

            }

            attachImage.setOnClickListener {
                listener.onMedia(post)
            }


            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener {item->
                        when(item.itemId){
                            R.id.remove -> {
                                listener.onRemote(post)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.edit -> {
                                listener.onEdit(post)
                                return@setOnMenuItemClickListener  true

                            }else -> false
                        }
                    }
                }.show()
            }

        }


    }
    private fun getAvatar(post: Post, binding: CardPostBinding){
        Glide.with(binding.authorAvatar)
            .load("${ru.netology.nmedia.adapter.BASE_URL}/avatars/${post.authorAvatar}")
            .placeholder(R.drawable.ic_baseline_account_box_24)
            .circleCrop()
            .timeout(10_000)
            .into(binding.authorAvatar)
    }
    private fun getAttachment(post: Post, binding: CardPostBinding){
        Glide.with(binding.attachImage)
            .load("${ru.netology.nmedia.adapter.BASE_URL}/media/${post.attachment?.url}")
            .error(R.drawable.ic_baseline_cancel_24)
            .timeout(10_000)
            .into(binding.attachImage)
    }
}
    class PostDiffCallback: DiffUtil.ItemCallback<FeedItem>(){
        override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            if (oldItem::class != newItem::class){
                return false
            }
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem == newItem
        }
    }

