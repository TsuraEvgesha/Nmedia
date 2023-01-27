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
import ru.netology.nmedia.databinding.FragmentRegistrationBinding
import ru.netology.nmedia.viewmodel.RegistrationViewModel

class RegistrationFragment : Fragment() {

    companion object {
        fun newInstance() = RegistrationFragment()

    }

    private  val viewModel: RegistrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegistrationBinding.inflate(inflater, container,false)


        binding.registerUserBtn.setOnClickListener {

            if (binding.loginEditText.text.toString().isNotEmpty() &&
                binding.passwordEditText.text.toString().isNotEmpty()&&
                binding.nameEditText.text.toString().isNotEmpty()&&
                binding.passwordEditText.text.toString() == binding.passwordCheckEditText.text.toString()){
                val login = binding.loginEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                val name = binding.nameEditText.text.toString()
                viewModel.registerUser(login,password,name)

            } else if (binding.passwordEditText.text.toString() != binding.passwordCheckEditText.text.toString()) {
                Snackbar.make(binding.root, getString(R.string.errorPassword), Snackbar.LENGTH_LONG).show()
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