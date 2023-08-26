package com.emircankirez.yummy.ui.presentation.userEdit

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.databinding.FragmentUserEditBinding
import com.emircankirez.yummy.domain.model.User
import com.emircankirez.yummy.ui.presentation.dialog.ErrorDialog
import com.emircankirez.yummy.ui.presentation.dialog.InfoDialog
import com.emircankirez.yummy.ui.presentation.dialog.LoadingDialog
import com.emircankirez.yummy.ui.presentation.dialog.SuccessDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserEditFragment : Fragment() {
    private var _binding : FragmentUserEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel : UserEditViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private var selectedUri : Uri? = null
    private lateinit var user: User

    // dialogs
    private var errorDialog: ErrorDialog? = null
    private var loadingDialog: LoadingDialog? = null
    private var successDialog: SuccessDialog? = null
    private var infoDialog: InfoDialog? = null

    @Inject
    lateinit var resourceProvider: ResourceProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        errorDialog = null
        loadingDialog = null
        successDialog = null
        infoDialog = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = UserEditFragmentArgs.fromBundle(requireArguments()).user

        registerLaunchers()
        updateUI(user)
        listen()
        observe()
    }

    private fun registerLaunchers(){
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
            selectedUri = uri
            binding.ivProfilePhoto.setImageURI(selectedUri)
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if(isGranted){
                galleryLauncher.launch("image/*")
            }else{
                showInfoDialog(resourceProvider.getString(R.string.permission_denied))
            }
        }
    }

    private fun updateUI(user: User){
        binding.apply {
            Glide.with(root).load(user.photoUrl).into(ivProfilePhoto)
            etName.setText(user.name)
            etSurname.setText(user.surname)
        }
    }

    private fun listen(){
        binding.fabSave.setOnClickListener {
            user.name = binding.etName.text.toString()
            user.surname = binding.etSurname.text.toString()
            viewModel.saveUserInformation(selectedUri, user)
        }

        binding.ivProfilePhoto.setOnClickListener {
            openGallery()
        }
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.saveUserInformationResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            hideLoadingDialog()
                            showErrorDialog(it.message){
                                findNavController().navigate(UserEditFragmentDirections.actionUserEditFragmentToFavoriteFragment())
                            }
                        }
                        Resource.Loading -> {
                            showLoadingDialog()
                        }
                        is Resource.Success -> {
                            hideLoadingDialog()
                            showSuccessDialog(resourceProvider.getString(R.string.profile_information_updated_successfully)){
                                findNavController().navigate(UserEditFragmentDirections.actionUserEditFragmentToFavoriteFragment())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openGallery(){
        // equal and above TIRAMISU version
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(requireView(), "Resim almak için izin lazım", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver"){
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }.show()
                }else{
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }else{
                // permission granted
                galleryLauncher.launch("image/*")
            }
        }else{
            // under TIRAMISU version
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(requireView(), "Resim almak için izin lazım", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver"){
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                }else{
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                // permission granted
                galleryLauncher.launch("image/*")
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

    private fun showInfoDialog(desc: String, callBack: () -> Unit = {}){
        if(infoDialog == null)
            infoDialog = InfoDialog(requireContext())

        infoDialog?.show(desc, callBack)
    }
}