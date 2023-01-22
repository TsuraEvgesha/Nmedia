package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentIdentificadeBinding


class IdentificadeFragment : Fragment() {

    private val fragList= listOf(
        SignInFragment.newInstance(),
        RegistrationFragment.newInstance())
    private lateinit var binding: FragmentIdentificadeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIdentificadeBinding.inflate(this.layoutInflater)
        binding.tb.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                parentFragmentManager.beginTransaction().replace(R.id.plase,fragList[tab?.position!!]).commit()

            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
            override fun onTabReselected(tab: TabLayout.Tab?) {


            }
        })



        return binding.root
    }


}