package com.emircankirez.yummy.domain.model

import android.os.Parcelable
import com.emircankirez.yummy.common.Constants.DEFAULT_PROFILE_PHOTO_URL
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String?,
    val email: String?,
    var name: String? = "",
    var surname: String? = "",
    var photoUrl: String? = DEFAULT_PROFILE_PHOTO_URL
) : Parcelable
