package com.emircankirez.yummy.data.remote.dto

import com.emircankirez.yummy.domain.model.CategoryMeal
import com.emircankirez.yummy.domain.model.Meal

data class MealListDto(
    val meals: List<MealDto>? = null
)

fun MealListDto.toMealList() : List<Meal> {
    return meals?.map { it.toMeal() } ?: listOf<Meal>()
}

fun MealListDto.toCategoryMealList() : List<CategoryMeal> {
    return meals?.map { it.toCategoryMeal() } ?: listOf<CategoryMeal>()
}