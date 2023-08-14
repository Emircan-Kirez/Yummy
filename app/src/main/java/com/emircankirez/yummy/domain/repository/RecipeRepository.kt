package com.emircankirez.yummy.domain.repository

import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.domain.model.Category
import com.emircankirez.yummy.domain.model.CategoryMeal
import com.emircankirez.yummy.domain.model.Meal
import kotlinx.coroutines.flow.Flow


interface RecipeRepository {

    suspend fun getCategories() : Flow<Resource<List<Category>>>

    suspend fun getRandomMeal() : Flow<Resource<List<Meal>>>

    suspend fun getCategoryMeals(categoryName: String) : Flow<Resource<List<CategoryMeal>>>

    suspend fun getMealById(mealId: String) : Flow<Resource<List<Meal>>>
}