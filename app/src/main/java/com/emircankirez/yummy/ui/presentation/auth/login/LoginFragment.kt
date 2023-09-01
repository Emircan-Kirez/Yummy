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
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.databinding.FragmentLoginBinding
import com.emircankirez.yummy.ui.MainActivity
import com.emircankirez.yummy.ui.presentation.dialog.ErrorDialog
import com.emircankirez.yummy.ui.presentation.dialog.LoadingDialog
import com.emircankirez.yummy.ui.presentation.dialog.SuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val navController: NavController by lazy { findNavController() }
    private val viewModel: LoginViewModel by viewModels()

    // dialogs
    private var errorDialog: ErrorDialog? = null
    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        errorDialog = null
        loadingDialog = null
        _binding = null
    }

    private fun listen() {
        binding.tvForgotPassword.setOnClickListener {
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
        }

        binding.tvDontYouHaveAccount.setOnClickListener {
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.apply {
            btnLogin.setOnClickListener {
                viewModel.login(etEmail.text.toString(), etPassword.text.toString())
            }
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.loginResponse.collect {
                    when (it) {
                        is Resource.Error -> {
                            hideLoadingDialog()
                            showErrorDialog(it.message)
                        }

                        Resource.Loading -> {
                            showLoadingDialog()
                        }

                        is Resource.Success -> {
                            hideLoadingDialog()
                            val intentToMainActivity =
                                Intent(requireContext(), MainActivity::class.java)
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

    private fun showErrorDialog(desc: String, callBack: () -> Unit = {}) {
        if (errorDialog == null)
            errorDialog = ErrorDialog(requireContext())

        errorDialog?.show(desc, callBack)
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null)
            loadingDialog = LoadingDialog(requireContext())
        loadingDialog?.show()
    }

    private fun hideLoadingDialog(callBack: () -> Unit = {}) {
        if (loadingDialog != null) {
            loadingDialog?.dismiss()
        }
        callBack.invoke()
    }
}