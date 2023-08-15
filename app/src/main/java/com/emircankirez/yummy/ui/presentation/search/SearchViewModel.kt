package com.emircankirez.yummy.ui.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.domain.model.CategoryMeal
import com.emircankirez.yummy.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
): ViewModel() {

    private var _searchResponse = MutableStateFlow<Resource<List<CategoryMeal>>>(Resource.Empty)
    val searchResponse : StateFlow<Resource<List<CategoryMeal>>> = _searchResponse

    fun getMealsByName(mealName: String) = viewModelScope.launch {
        recipeRepository.getMealsByName(mealName).collect{ result ->
            when(result){
                Resource.Empty -> {}
                is Resource.Error -> {
                    _searchResponse.value = Resource.Error(result.message)
                }
                Resource.Loading -> {
                    _searchResponse.value = Resource.Loading
                }
                is Resource.Success -> {
                    _searchResponse.value = Resource.Success(result.data)
                }
            }
        }
    }
}