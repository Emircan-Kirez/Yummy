package com.emircankirez.yummy.ui.presentation.mealDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.domain.model.Meal
import com.emircankirez.yummy.domain.repository.FirebaseRepository
import com.emircankirez.yummy.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private var _isFavorite = MutableStateFlow(false)
    val isFavorite : StateFlow<Boolean> = _isFavorite

    private var _mealResponse = MutableStateFlow<Resource<List<Meal>>>(Resource.Empty)
    val mealResponse : StateFlow<Resource<List<Meal>>> = _mealResponse

    private var _addResponse = MutableStateFlow<Resource<Unit>>(Resource.Empty)
    val addResponse : StateFlow<Resource<Unit>> = _addResponse

    private var _removeResponse = MutableStateFlow<Resource<Unit>>(Resource.Empty)
    val removeResponse : StateFlow<Resource<Unit>> = _removeResponse

    fun isExists(mealId: String) = viewModelScope.launch {
        firebaseRepository.isFavorite(mealId).collect{ isFavorite ->
            when(isFavorite){
                Resource.Empty -> {}
                is Resource.Error -> {
                    _mealResponse.value = Resource.Error(isFavorite.message)
                }
                Resource.Loading -> {
                    _mealResponse.value = Resource.Loading
                }
                is Resource.Success -> {
                    getMealById(isFavorite.data, mealId)
                }
            }
        }
    }

    private fun getMealById(isFavorite: Boolean, mealId: String) = viewModelScope.launch {
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
                    _isFavorite.value = isFavorite
                    _mealResponse.value = Resource.Success(result.data)
                }
            }
        }
    }

    fun favoriteOnClicked() {
        val resource = _mealResponse.value
        if (resource is Resource.Success) {
            val meal = resource.data[0]
            if (_isFavorite.value)
                removeFavoriteMeal(meal)
            else
                addFavoriteMeal(meal)
        }
    }

    private fun removeFavoriteMeal(meal: Meal) = viewModelScope.launch {
        firebaseRepository.removeFavorite(meal.id).collect{ result ->
            when(result){
                Resource.Empty -> {}
                is Resource.Error -> {
                    _removeResponse.value = Resource.Error(result.message)
                }
                Resource.Loading -> {
                    _removeResponse.value = Resource.Loading
                }
                is Resource.Success -> {
                    _isFavorite.value = false
                    _removeResponse.value = Resource.Success(Unit)
                }
            }
         }
    }

    private fun addFavoriteMeal(meal: Meal) = viewModelScope.launch {
        firebaseRepository.addFavorite(meal).collect{ result ->
            when(result){
                Resource.Empty -> {}
                is Resource.Error -> {
                    _addResponse.value = Resource.Error(result.message)
                }
                Resource.Loading -> {
                    _addResponse.value = Resource.Loading
                }
                is Resource.Success -> {
                    _isFavorite.value = true
                    _addResponse.value = Resource.Success(Unit)
                }
            }
        }
    }
}