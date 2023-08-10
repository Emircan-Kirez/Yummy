package com.emircankirez.yummy.data.remote.dto

import com.emircankirez.yummy.domain.model.Category

data class CategoryListDto(
    val categories: List<CategoryDto>
)

fun CategoryListDto.toCategoryList() : List<Category> {
    return categories.map { it.toCategory() }
}