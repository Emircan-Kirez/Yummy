package com.emircankirez.yummy.ui.presentation.mealDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.domain.model.Meal
import com.emircankirez.yummy.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
): ViewModel() {

    private var _mealResponse = MutableStateFlow<Resource<List<Meal>>>(Resource.Empty)
    val mealResponse : StateFlow<Resource<List<Meal>>> = _mealResponse

    fun getMealById(mealId: String) = viewModelScope.launch {
        recipeRepository.getMealById(mealId).collect{ result ->
            when(result){
                Resource.Empty -> {}
                is Resource.Error -> {
                    _mealResponse.value = Resource.Error(result.message)
                }
                Resource.Loading -> {
                    _mealResponse.value = Resource.Loading
                }
                is Resource.Success -> {
                    _mealResponse.value = Resource.Success(result.data)
                }
            }
        }
    }
}