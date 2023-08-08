package com.emircankirez.yummy.data.model

data class User(
    val uid: String,
    val email: String,
    val name: String? = null,
    val surname: String? = null
)