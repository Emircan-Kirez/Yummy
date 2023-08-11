package com.emircankirez.yummy.ui.presentation.categoryMeal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.domain.model.CategoryMeal
import com.emircankirez.yummy.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CategoryMealViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
): ViewModel() {

    var searchQuery = MutableStateFlow<String>("")
    private var _originalCategoryMealResponse = MutableStateFlow<Resource<List<CategoryMeal>>>(Resource.Empty)
    private var _filteredCategoryMealResponse = MutableStateFlow<Resource<List<CategoryMeal>>>(Resource.Empty)
    val filteredCategoryMealResponse : StateFlow<Resource<List<CategoryMeal>>> = _filteredCategoryMealResponse

    init {
        viewModelScope.launch{
            searchQuery.flatMapLatest { query ->
                if (query.isEmpty()) {
                    _originalCategoryMealResponse
                } else {
                    _originalCategoryMealResponse.map { resource ->
                        if (resource is Resource.Success)
                            Resource.Success(
                                resource.data.filter {
                                    it.name.contains(query, true)
                                })
                        else
                            resource
                    }
                }
            }.collect {
                _filteredCategoryMealResponse.value = it
            }
        }
    }

    fun getCategoryMeals(categoryName: String) = viewModelScope.launch {
        recipeRepository.getCategoryMeals(categoryName).collect{ result ->
            when(result){
                Resource.Empty -> {}
                is Resource.Error -> {
                    _originalCategoryMealResponse.value = Resource.Error(result.message)
                }
                Resource.Loading -> {
                    _originalCategoryMealResponse.value = Resource.Loading
                }
                is Resource.Success -> {
                    _originalCategoryMealResponse.value = Resource.Success(result.data)
                }
            }
        }
    }

}