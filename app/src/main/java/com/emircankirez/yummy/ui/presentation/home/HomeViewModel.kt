package com.emircankirez.yummy.ui.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.domain.model.Category
import com.emircankirez.yummy.domain.model.Meal
import com.emircankirez.yummy.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private var _categoryResponse = MutableStateFlow<Resource<List<Category>>>(Resource.Empty)
    val categoryResponse : StateFlow<Resource<List<Category>>> = _categoryResponse

    private var _randomMealResponse = MutableStateFlow<Resource<List<Meal>>>(Resource.Empty)
    val randomMealResponse : StateFlow<Resource<List<Meal>>> = _randomMealResponse

    init {
        getCategories()
        getRandomMeal()
    }

    private fun getCategories() = viewModelScope.launch {
        recipeRepository.getCategories().collect{ result ->
            when(result) {
                Resource.Empty -> {}
                is Resource.Error -> {
                    _categoryResponse.value = Resource.Error(result.message)
                }
                Resource.Loading -> {
                    _categoryResponse.value = Resource.Loading
                }
                is Resource.Success -> {
                    _categoryResponse.value = Resource.Success(result.data)
                }
            }
        }
    }

    private fun getRandomMeal() = viewModelScope.launch {
        recipeRepository.getRandomMeal().collect{ result ->
            when(result) {
                Resource.Empty -> {}
                is Resource.Error -> {
                    _randomMealResponse.value = Resource.Error(result.message)
                }
                Resource.Loading -> {
                    _randomMealResponse.value = Resource.Loading
                }
                is Resource.Success -> {
                    _randomMealResponse.value = Resource.Success(result.data)
                }
            }
        }
    }
}