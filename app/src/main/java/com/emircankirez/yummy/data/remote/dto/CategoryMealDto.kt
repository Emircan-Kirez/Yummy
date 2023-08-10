package com.emircankirez.yummy.data.remote.dto

import com.emircankirez.yummy.domain.model.CategoryMeal

data class CategoryMealDto(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String
)

fun CategoryMealDto.toCategoryMeal() : CategoryMeal {
    return CategoryMeal(
        id = idMeal,
        name = strMeal,
        photoUrl = strMealThumb
    )
}