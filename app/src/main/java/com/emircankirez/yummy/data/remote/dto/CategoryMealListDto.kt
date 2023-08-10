package com.emircankirez.yummy.data.remote.dto

import com.emircankirez.yummy.domain.model.CategoryMeal

data class CategoryMealListDto(
    val meals: List<CategoryMealDto>
)

fun CategoryMealListDto.toCategoryMealList() : List<CategoryMeal> {
    return meals.map { it.toCategoryMeal() }
}