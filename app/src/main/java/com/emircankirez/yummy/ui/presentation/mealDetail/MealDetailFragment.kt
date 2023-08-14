package com.emircankirez.yummy.ui.presentation.mealDetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
import com.bumptech.glide.Glide
import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.common.extensions.hide
import com.emircankirez.yummy.common.extensions.show
import com.emircankirez.yummy.databinding.FragmentMealDetailBinding
import com.emircankirez.yummy.domain.model.Meal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MealDetailFragment : Fragment() {
    private var _binding: FragmentMealDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MealDetailViewModel by viewModels()
    private val navController: NavController by lazy { findNavController() }
    private lateinit var meal: Meal
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
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mealId = MealDetailFragmentArgs.fromBundle(requireArguments()).mealId

        viewModel.getMealById(mealId)
        listen()
        observe()
    }

    private fun listen(){
        binding.ibFavorite.setOnClickListener {
            // buton doldur ya da outline
            // firebase favorilerden sil ya da favorilere ekle
        }

        binding.ibYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meal.youtubeUrl))
            startActivity(intent)
        }
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.mealResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            errorCase()
                            Toast.makeText(requireContext(), "Yemek bilgileri alınamadı.", Toast.LENGTH_SHORT).show()
                            // error dialog ok tıklanınca geri gönder, cancelable false yap ki tıklamak zorunda olsun
                            navController.popBackStack()
                        }
                        Resource.Loading -> {
                            loadingCase()
                        }
                        is Resource.Success -> {
                            meal = it.data[0]
                            delay(500) // animasyonu gösterebilmek için
                            bind(meal)
                            successCase()
                        }
                    }
                }
            }
        }
    }

    private fun bind(meal: Meal){
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
}