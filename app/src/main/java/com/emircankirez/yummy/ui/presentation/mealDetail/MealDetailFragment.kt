package com.emircankirez.yummy.ui.presentation.mealDetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.common.extensions.hide
import com.emircankirez.yummy.common.extensions.show
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.databinding.FragmentMealDetailBinding
import com.emircankirez.yummy.domain.model.Meal
import com.emircankirez.yummy.ui.presentation.dialog.ErrorDialog
import com.emircankirez.yummy.ui.presentation.dialog.LoadingDialog
import com.emircankirez.yummy.ui.presentation.dialog.SuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MealDetailFragment : Fragment() {
    private var _binding: FragmentMealDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MealDetailViewModel by viewModels()
    private val navController: NavController by lazy { findNavController() }
    private lateinit var meal: Meal

    // dialogs
    private var errorDialog: ErrorDialog? = null
    private var loadingDialog: LoadingDialog? = null
    private var successDialog: SuccessDialog? = null

    @Inject
    lateinit var resourceProvider: ResourceProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMealDetailBinding.inflate(inflater, container, false)
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
        val mealId = MealDetailFragmentArgs.fromBundle(requireArguments()).mealId

        viewModel.isExists(mealId)
        listen()
        observe()
    }

    private fun listen(){
        binding.ibFavorite.setOnClickListener {
            viewModel.favoriteOnClicked()
        }

        binding.ibYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meal.youtubeUrl))
            startActivity(intent)
        }
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.isFavorite.collect{
                    updateFavoriteButton(it)
                }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.mealResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            errorCase()
                            showErrorDialog(it.message){
                                navController.popBackStack()
                            }
                        }
                        Resource.Loading -> {
                            loadingCase()
                        }
                        is Resource.Success -> {
                            meal = it.data[0]
                            delay(500) // animasyonu gösterebilmek için
                            updateUI(meal)
                            successCase()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.addResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            hideLoadingDialog()
                            showErrorDialog(resourceProvider.getString(R.string.couldnt_add_to_favorites))
                        }
                        Resource.Loading -> {
                            showLoadingDialog()
                        }
                        is Resource.Success -> {
                            hideLoadingDialog{
                                showSuccessDialog(resourceProvider.getString(R.string.add_to_favorites_successfully))
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.removeResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            hideLoadingDialog()
                            showErrorDialog(resourceProvider.getString(R.string.couldnt_remove_from_favorites))
                        }
                        Resource.Loading -> {
                            showLoadingDialog()
                        }
                        is Resource.Success -> {
                            hideLoadingDialog {
                                showSuccessDialog(resourceProvider.getString(R.string.remove_from_favorites_successfully))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite)
            binding.ibFavorite.setBackgroundResource(R.drawable.ic_favorite)
        else
            binding.ibFavorite.setBackgroundResource(R.drawable.ic_favorite_outline)
    }

    private fun updateUI(meal: Meal){
        binding.apply {
            Glide.with(binding.root).load(meal.photoUrl).into(ivMealPhoto)
            tvMealName.text = meal.name
            tvMealCategory.text = getString(R.string.categoryName, meal.category)
            tvMealArea.text = getString(R.string.areaName, meal.area)
            tvInstructionDetail.text = meal.instructions
        }
    }

    private fun loadingCase(){
        binding.apply {
            progressBar.show()
            ivMealPhoto.hide()
            ibYoutube.hide()
            ibFavorite.hide()
            tvMealName.hide()
            tvMealCategory.hide()
            tvMealArea.hide()
            tvInstruction.hide()
            tvInstructionDetail.hide()
        }
    }

    private fun errorCase(){
        binding.apply {
            progressBar.hide()
            ivMealPhoto.hide()
            ibYoutube.hide()
            ibFavorite.hide()
            tvMealName.hide()
            tvMealCategory.hide()
            tvMealArea.hide()
            tvInstruction.hide()
            tvInstructionDetail.hide()
        }
    }
    private fun successCase(){
        binding.apply {
            progressBar.hide()
            ivMealPhoto.show()
            ibYoutube.show()
            ibFavorite.show()
            tvMealName.show()
            tvMealCategory.show()
            tvMealArea.show()
            tvInstruction.show()
            tvInstructionDetail.show()
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