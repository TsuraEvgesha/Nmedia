package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import java.math.RoundingMode
import java.text.DecimalFormat



class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(
        ownerProducer= ::requireParentFragment
    )
    companion object {
        private  const val TEXT_KEY="TEXT_KEY"
        var Bundle.textArg: String?
            set(value) = putString(TEXT_KEY, value)
            get() = getString(TEXT_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter (object : PostListener {

            override fun onEdit(post: Post) {
                viewModel.edit(post)

            }
            override fun onLike(post: Post) {
                if (post.likedByMe){
                    viewModel.dislikeById(post.id)
                } else viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action=Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT,post.content)
                    type="text/plan"
                }
                val shareIntent = Intent.createChooser(intent,getString(R.string.share))
                startActivity(shareIntent)


            }

            override fun onRemote(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onPlayVideo(post: Post) {
                Intent(Intent.ACTION_VIEW,
                    Uri.parse(post.video)).apply {
                    if (requireContext().packageManager != null){
                        startActivity(this)
                    }
                }
            }

            override fun onPost(post: Post) {
                val action=FeedFragmentDirections.actionFeedFragment2ToPostFragment(post.id.toInt())
                findNavController().navigate(action)

            }

            override fun onMedia(post: Post) {
                val action = FeedFragmentDirections.actionFeedFragment2ToMediaFragment(post.attachment?.url.toString())
                findNavController().navigate(action)
            }
        }
        )

        binding.list.adapter=adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiprefresh.isRefreshing = state.refreshing
            if (state.error){
                Snackbar.make(binding.root,R.string.error_loading,Snackbar.LENGTH_LONG)
                    .setAction("Retry"){viewModel.loadPosts()}
                    .show()
            }
        }
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyPostMes.isVisible=state.empty
//            val sizeList = state.posts.size

        }

        binding.swiprefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }
        binding.retry.setOnClickListener {
            viewModel.loadPosts()
        }
        binding.create.setOnClickListener{
            findNavController().navigate(R.id.action_feedFragment2_to_newPostFragment2)
        }
        viewModel.newerCount.observe(viewLifecycleOwner) {state ->
            if(state !=0){
                val text = getString(R.string.NewPost) + " ($state)"
                binding.newPosts.text= text
                binding.newPosts.visibility=View.VISIBLE
            }
            binding.newPosts.setOnClickListener {
                viewModel.loadPosts()
                viewModel.updateStatus()
                it.visibility = View.GONE
                adapter.registerAdapterDataObserver(
                    object : RecyclerView.AdapterDataObserver() {
                        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                            if (positionStart == 0) {
                                binding.list.smoothScrollToPosition(0)
                            }
                        }
                    }
                )



            }

        }




//
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.nav_host_fragment_container,PostFragment.newInstance("1","2"))
//            .commit()
        return binding.root
    }
    private fun toastOnError(requestCode: Int) {
        if (requestCode.toString().startsWith("1")) {
            Toast.makeText(context, "Информационный код ответа", Toast.LENGTH_SHORT).show()
        }
        if (requestCode.toString().startsWith("3")) {
            Toast.makeText(context, "Перенаправление", Toast.LENGTH_SHORT).show()
        }
        if (requestCode.toString().startsWith("4")) {
            Toast.makeText(context, "Ошибка клиента", Toast.LENGTH_SHORT).show()
        }
        if (requestCode.toString().startsWith("5")) {
            Toast.makeText(context, "Ошибка сервера", Toast.LENGTH_SHORT).show()
        }
    }


}




fun counter(item: Long): String {
    return when (item) {
        in 1000..1099 -> {
            val num = numberToInt(item / 1000.0)
            (num + "K") }
        in 1100..9999 -> {
            val num = numberToInt(item / 1000.0)
            (num + "K") }
        in 10_000..999_999 -> {
            ((item / 1000).toString() + "K") }
        in 1_000_000..1_000_000_000 -> {
            val num = numberToInt(item / 1_000_000.0)
            (num + "M") }
        else -> item.toString()
    }
}

private fun numberToInt (number: Double): String {
    val demFormat = DecimalFormat("#.#")
    demFormat.roundingMode = RoundingMode.FLOOR
    return demFormat.format(number).toString()
}




