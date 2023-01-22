package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewmodel.SignInViewModel

class SignInFragment : Fragment() {

    companion object {
        fun newInstance() = SignInFragment()
    }

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)

        binding.loginBtn.setOnClickListener {
            if (binding.loginEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty() ){
                val login = binding.loginEditText.text.toString()
                val pass = binding.passwordEditText.text.toString()
                viewModel.updateUser(login,pass)

            } else Snackbar.make(binding.root, getString(R.string.errorPol), Snackbar.LENGTH_LONG).show()
        }
        viewModel.tokenReceived.observe(viewLifecycleOwner) {
            if (it == 0){
                findNavController().navigateUp()
            } else {
                Snackbar.make(binding.root, getString(R.string.errorLogPass), Snackbar.LENGTH_LONG).show()
            }
        }
        return binding.root
    }



}