package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import ru.netology.nmedia.adapter.fragmentAdapter
import ru.netology.nmedia.databinding.FragmentIdentificadeBinding


class IdentificadeFragment : Fragment() {

    private val fragList= listOf(
        SignInFragment.newInstance(),
        RegistrationFragment.newInstance())
    private val fragListTitle= listOf(
        "Sign In",
        "Sign Up")
    private lateinit var binding: FragmentIdentificadeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIdentificadeBinding.inflate(this.layoutInflater)
        val adapter = fragmentAdapter(this,fragList)
        binding.placeHolder.adapter=adapter
        TabLayoutMediator(binding.tb,binding.placeHolder){
            tab, pos -> tab.text=fragListTitle[pos]
        }.attach()

        return binding.root
    }


}