package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.adapter.PostLoadingStateAdapter
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject


@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    @Inject
    lateinit var appAuth: AppAuth

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
                if (appAuth.authStateFlow.value.id != 0L) {
                viewModel.edit(post)}
                else {
                    showSignInDialog()
                }

            }
            override fun onLike(post: Post) {
                if (appAuth.authStateFlow.value.id != 0L) {
                    if (post.likedByMe) {
                        viewModel.dislikeById(post.id)
                    } else {
                        viewModel.likeById(post.id)
                    }
                } else showSignInDialog()
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
                if (appAuth.authStateFlow.value.id != 0L) {
                viewModel.removeById(post.id)
            } else {
                    showSignInDialog()
            }
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
//                val action=FeedFragmentDirections.actionFeedFragment2ToPostFragment()
//                findNavController().navigate(action)

            }

            override fun onMedia(post: Post) {
                val action = FeedFragmentDirections.actionFeedFragment2ToMediaFragment(post.attachment?.url.toString())
                findNavController().navigate(action)
            }
        }
        )

//        binding.list.adapter=adapter
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter{
                adapter.retry()},
            footer = PostLoadingStateAdapter{
                adapter.retry()
            }
        )

        binding.list.addItemDecoration(
            DividerItemDecoration(binding.list.context, DividerItemDecoration.VERTICAL)
        )
//        lifecycleScope.launchWhenCreated {
//            viewModel.data.collectLatest {
//                adapter.submitData(it)
//            }
//        }
//        viewModel.dataState.observe(viewLifecycleOwner) { state ->
//            binding.progress.isVisible = state.loading
//            binding.swiprefresh.isRefreshing = state.refreshing
//            if (state.error){
//                Snackbar.make(binding.root,R.string.error_loading,Snackbar.LENGTH_LONG)
//                    .setAction(getString(R.string.retry_loading)){viewModel.loadPosts()}
//                    .show()
//            }
//
//        }
        viewModel.tokenReceived.observe(viewLifecycleOwner){
            adapter.refresh()
        }


        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->

                binding.swiprefresh.isRefreshing = state.refresh is LoadState.Loading
//                        || state.append is LoadState.Loading
//                        || state.prepend is LoadState.Loading

            }
        }



        binding.swiprefresh.setOnRefreshListener {
            adapter.refresh()
        }
//        binding.retry.setOnClickListener {
//            viewModel.loadPosts()
//        }
        binding.create.setOnClickListener{
            if(appAuth.authStateFlow.value.token != null){
                findNavController().navigate(R.id.action_feedFragment2_to_newPostFragment2)
            } else showSignInDialog()

        }

//        viewModel.newerCount.observe(viewLifecycleOwner) {state ->
//            if(state !=0){
//                val text = getString(R.string.NewPost) + " ($state)"
//                binding.newPosts.text= text
//                binding.newPosts.visibility=View.VISIBLE
//            }
//            binding.newPosts.setOnClickListener {
//                viewModel.loadPosts()
//                viewModel.updateStatus()
//                it.visibility = View.GONE
//                adapter.registerAdapterDataObserver(
//                    object : RecyclerView.AdapterDataObserver() {
//                        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                            if (positionStart == 0) {
//                                binding.list.smoothScrollToPosition(0)
//                            }
//                        }
//                    }
//                )
//            }
//
//        }
        return binding.root
    }
    private fun showSignInDialog(){
        val listener = DialogInterface.OnClickListener{ _, which->
            when(which) {
                DialogInterface.BUTTON_POSITIVE -> findNavController().navigate(R.id.action_feedFragment2_to_identificadeFragment)
                DialogInterface.BUTTON_NEGATIVE -> Toast.makeText(context, getString(R.string.warningAuth), Toast.LENGTH_SHORT).show()
            }
        }
        val dialog = AlertDialog.Builder(context)
            .setCancelable(false)
            .setTitle(getString(R.string.Authorization))
            .setMessage(getString(R.string.AuthorizationMes))
            .setPositiveButton(getString(R.string.sign_in), listener)
            .setNegativeButton(getString(R.string.Later), listener)
            .create()

        dialog.show()
    }

    private fun toastOnError(requestCode: Int) {
        if (requestCode.toString().startsWith("1")) {
            Toast.makeText(context, getString(R.string.Code1), Toast.LENGTH_SHORT).show()
        }
        if (requestCode.toString().startsWith("3")) {
            Toast.makeText(context, getString(R.string.Code2), Toast.LENGTH_SHORT).show()
        }
        if (requestCode.toString().startsWith("4")) {
            Toast.makeText(context, getString(R.string.errorCode1), Toast.LENGTH_SHORT).show()
        }
        if (requestCode.toString().startsWith("5")) {
            Toast.makeText(context, getString(R.string.errorCode2), Toast.LENGTH_SHORT).show()
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




