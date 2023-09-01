package com.emircankirez.yummy.ui.presentation.auth.forgotPassword

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
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.databinding.FragmentForgotPasswordBinding
import com.emircankirez.yummy.ui.presentation.dialog.ErrorDialog
import com.emircankirez.yummy.ui.presentation.dialog.LoadingDialog
import com.emircankirez.yummy.ui.presentation.dialog.SuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val navController: NavController by lazy { findNavController() }
    private val viewModel: ForgotPasswordViewModel by viewModels()

    @Inject
    lateinit var resourceProvider: ResourceProvider

    // dialogs
    private var errorDialog: ErrorDialog? = null
    private var loadingDialog: LoadingDialog? = null
    private var successDialog: SuccessDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        errorDialog = null
        loadingDialog = null
        successDialog = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listen()
        observe()
    }

    private fun listen(){
        binding.btnResetPassword.setOnClickListener {
            viewModel.sendPasswordResetLink(binding.etEmail.text.toString())
        }
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.forgotPasswordResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            hideLoadingDialog()
                            showErrorDialog(it.message)
                        }
                        Resource.Loading -> {
                           showLoadingDialog()
                        }
                        is Resource.Success -> {
                            hideLoadingDialog()
                            showSuccessDialog(resourceProvider.getString(R.string.send_password_reset_email_successfully)){
                                navController.popBackStack()
                            }
                        }
                    }
                    viewModel.resetForgotPasswordResponse()
                }
            }
        }
    }

    private fun showErrorDialog(desc: String, callBack: () -> Unit = {}){
        if(errorDialog == null)
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

    private fun showSuccessDialog(desc: String, callBack: () -> Unit = {}){
        if(successDialog == null)
            successDialog = SuccessDialog(requireContext())

        successDialog?.show(desc, callBack)
    }
}