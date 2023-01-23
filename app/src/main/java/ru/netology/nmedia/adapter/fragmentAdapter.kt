package ru.netology.nmedia.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.netology.nmedia.activity.IdentificadeFragment

class fragmentAdapter(fa:IdentificadeFragment,private val list: List<Fragment>):FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}