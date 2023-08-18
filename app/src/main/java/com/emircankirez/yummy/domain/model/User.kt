package com.emircankirez.yummy.domain.model

import com.emircankirez.yummy.common.Constants.DEFAULT_PROFILE_PHOTO_URL

data class User(
    val uid: String?,
    val email: String?,
    val name: String? = "",
    val surname: String? = "",
    val photoUrl: String? = DEFAULT_PROFILE_PHOTO_URL
)
