package com.emircankirez.yummy.ui.presentation.auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.common.extensions.isValidEmail
import com.emircankirez.yummy.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val navController: NavController by lazy { findNavController() }
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listen()
        observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listen(){
        binding.tvHaveAlreadyAccount.setOnClickListener {
            navController.popBackStack()
        }

        binding.apply {
            btnRegister.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if(email.isNotBlank() && password.isNotBlank()){
                    if(email.isValidEmail())
                        viewModel.register(email, password)
                    else
                        viewModel.setRegisterResponseError(getString(R.string.not_valid_email))
                }else{
                    viewModel.setRegisterResponseError(getString(R.string.empty_email_or_password_field))
                }
            }
        }
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.registerResponse.collect{
                    when(it){
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        Resource.Loading -> {
                            // loading alert dialog
                        }
                        is Resource.Success -> {
                            Toast.makeText(requireContext(), "Kayıt başarılı bir şekilde oluşturuldu.", Toast.LENGTH_SHORT).show()
                            navController.navigate(R.id.action_registerFragment_to_homeFragment)
                        }
                        Resource.Empty -> {}
                    }
                }
            }
        }
    }
}