package com.emircankirez.yummy.data.remote
import com.emircankirez.yummy.data.remote.dto.CategoryListDto
import com.emircankirez.yummy.data.remote.dto.CategoryMealListDto
import com.emircankirez.yummy.data.remote.dto.MealListDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {

    @GET("categories.php")
    suspend fun getCategories() : CategoryListDto

    @GET("filter.php")
    suspend fun getMealsByCategoryName(@Query("c") categoryName: String) : CategoryMealListDto

    @GET("lookup.php")
    suspend fun getMealById(@Query("i") recipeId: Int) : MealListDto

    @GET("search.php")
    suspend fun getMealsByName(@Query("s") recipeName: String) : MealListDto

    @GET("random.php")
    suspend fun getRandomMeal() : MealListDto
}