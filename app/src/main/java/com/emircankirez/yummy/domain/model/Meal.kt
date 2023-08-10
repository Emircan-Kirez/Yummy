package com.emircankirez.yummy.domain.model

data class Meal(
    val id: String,
    val area: String,
    val category: String,
    val instructions: String,
    val name: String,
    val photoUrl: String,
    val youtubeUrl: String
)