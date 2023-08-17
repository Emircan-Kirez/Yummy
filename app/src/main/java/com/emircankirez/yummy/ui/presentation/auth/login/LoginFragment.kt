package com.emircankirez.yummy.ui.presentation.auth.login

import android.content.Intent
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
import com.emircankirez.yummy.databinding.FragmentLoginBinding
import com.emircankirez.yummy.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val navController: NavController by lazy { findNavController() }
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
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
        binding.tvForgotPassword.setOnClickListener {
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
        }

        binding.tvDontYouHaveAccount.setOnClickListener {
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.apply {
            btnLogin.setOnClickListener {
                viewModel.login(requireContext(), etEmail.text.toString(), etPassword.text.toString())
            }
        }
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.loginResponse.collect{
                    when(it){
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        Resource.Loading -> {
                            // loading alert dialog
                        }
                        is Resource.Success -> {
                            val intentToMainActivity = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intentToMainActivity)
                            requireActivity().finish()
                        }
                        Resource.Empty -> {}
                    }
                    viewModel.resetLoginResponse()
                }
            }
        }
    }
}