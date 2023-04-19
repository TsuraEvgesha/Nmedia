package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.FragmentAdapter
import ru.netology.nmedia.databinding.FragmentIdentificadeBinding


class IdentificadeFragment : Fragment() {

    private val fragList= listOf(
        SignInFragment.newInstance(),
        RegistrationFragment.newInstance())
    private val fragListTitle: List<String>
        get() = listOf(
            getString(R.string.sign_in),
            getString(R.string.sign_up))
    private lateinit var binding: FragmentIdentificadeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIdentificadeBinding.inflate(this.layoutInflater)
        val adapter = FragmentAdapter(this,fragList)
        binding.placeHolder.adapter=adapter
        TabLayoutMediator(binding.tb,binding.placeHolder){
            tab, pos -> tab.text=fragListTitle[pos]
        }.attach()

        return binding.root
    }


}