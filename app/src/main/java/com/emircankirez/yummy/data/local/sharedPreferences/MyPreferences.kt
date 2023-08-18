package com.emircankirez.yummy.data.local.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import com.emircankirez.yummy.R
import com.emircankirez.yummy.data.provider.ResourceProvider
import javax.inject.Inject

class MyPreferences @Inject constructor(
    context: Context,
    private val resourceProvider: ResourceProvider
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        MyPreferencesConfig.SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    var isLogin: Boolean
        get() = sharedPreferences.getBoolean(MyPreferencesConfig.IS_LOGIN, false)
        set(value) = editor.putBoolean(MyPreferencesConfig.IS_LOGIN, value).apply()

    var userUid: String?
        get() = sharedPreferences.getString(MyPreferencesConfig.USER_UID, resourceProvider.getString(R.string.not_found_user_uid))
        set(value) = editor.putString(MyPreferencesConfig.USER_UID, value).apply()
}