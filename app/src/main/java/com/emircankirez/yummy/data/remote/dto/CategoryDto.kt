package com.emircankirez.yummy.data.remote.dto

import com.emircankirez.yummy.domain.model.Category

data class CategoryDto(
    val idCategory: String,
    val strCategory: String,
    val strCategoryDescription: String,
    val strCategoryThumb: String
)

fun CategoryDto.toCategory() : Category {
    return Category(
        name = strCategory,
        photoUrl = strCategoryThumb
    )
}