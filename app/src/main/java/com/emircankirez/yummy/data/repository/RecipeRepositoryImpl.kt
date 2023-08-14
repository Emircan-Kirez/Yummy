package com.emircankirez.yummy.data.repository

import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.data.remote.RecipeApi
import com.emircankirez.yummy.data.remote.dto.toCategoryList
import com.emircankirez.yummy.data.remote.dto.toCategoryMealList
import com.emircankirez.yummy.data.remote.dto.toMealList
import com.emircankirez.yummy.domain.model.Category
import com.emircankirez.yummy.domain.model.CategoryMeal
import com.emircankirez.yummy.domain.model.Meal
import com.emircankirez.yummy.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipeApi : RecipeApi
) : RecipeRepository {
    override suspend fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading)
        try {
            val categories = recipeApi.getCategories().toCategoryList()
            emit(Resource.Success(categories))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: "Bilinmeyen Hata"))
        }
    }

    override suspend fun getRandomMeal(): Flow<Resource<List<Meal>>> = flow {
        emit(Resource.Loading)
        try{
            val randomMeal = recipeApi.getRandomMeal().toMealList()
            emit(Resource.Success(randomMeal))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: "Bilinmeyen Hata"))
        }
    }

    override suspend fun getCategoryMeals(categoryName: String): Flow<Resource<List<CategoryMeal>>> = flow {
        emit(Resource.Loading)
        try{
            val categoryMealList = recipeApi.getMealsByCategoryName(categoryName).toCategoryMealList()
            emit(Resource.Success(categoryMealList))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: "Bilinmeyen Hata"))
        }
    }

    override suspend fun getMealById(mealId: String): Flow<Resource<List<Meal>>> = flow {
        emit(Resource.Loading)
        try {
            val meal = recipeApi.getMealById(mealId).toMealList()
            emit(Resource.Success(meal))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: "Bilinmeyen Hata"))
        }
    }
}