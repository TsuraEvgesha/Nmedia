package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentMediaBinding

const val BASE_URL = "http://10.0.2.2:9999"
class MediaFragment: Fragment() {



    private val args by navArgs<MediaFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMediaBinding.inflate(
            inflater,
            container,
            false
        )
        val url = args.media.toUri()

        Glide.with(binding.imageFull)
            .load("${BASE_URL}/media/$url")
            .error(R.drawable.ic_baseline_cancel_24)
            .timeout(10_000)
            .into(binding.imageFull)


        return binding.root
    }
}
