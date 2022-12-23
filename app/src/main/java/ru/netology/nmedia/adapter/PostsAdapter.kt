package ru.netology.nmedia.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.counter
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post


const val BASE_URL = "http://10.0.2.2:9999"
internal class PostsAdapter (private val listener: PostListener): ListAdapter <Post, PostsAdapter.PostViewHolder>(PostDiffCallback())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardPostBinding.inflate(inflater, parent, false)
        return  PostViewHolder(
            binding,
            listener
        )
    }
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }


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

        binding.apply {
            author.text = post.author
            published.text = post.published.toString()
            content.text = post.content
            share.isChecked = post.shareReal
            like.isChecked = post.likedByMe
            share.text = counter(post.share)
            like.text = counter(post.likes.toLong())
            videoGroup.isVisible = post.video !=null



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


            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
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
//            root.setOnClickListener {
//                listener.onPost(post)
//            }
        }


    }
    private fun getAvatar(post: Post, binding: CardPostBinding){
        Glide.with(binding.authorAvatar)
            .load("$BASE_URL/avatars/${post.authorAvatar}")
            .placeholder(R.drawable.ic_baseline_account_box_24)
            .circleCrop()
            .timeout(10_000)
            .into(binding.authorAvatar)
    }
    private fun getAttachment(post: Post, binding: CardPostBinding){
        Glide.with(binding.attachImage)
            .load("$BASE_URL/images/${post.attachment?.url}")
            .error(R.drawable.ic_baseline_cancel_24)
            .timeout(10_000)
            .into(binding.attachImage)
    }
}
    class PostDiffCallback: DiffUtil.ItemCallback<Post>(){
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
