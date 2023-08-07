package com.emircankirez.yummy.data.local.sharedPreferences

import android.content.Context
import android.content.SharedPreferences

class MyPreferences private constructor(
    context: Context
) {
    companion object {
        private var myPreferences: MyPreferences? = null

        fun getInstance(context: Context) : MyPreferences {
            if(myPreferences == null)
                myPreferences = MyPreferences(context)
            return myPreferences!!
        }
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        MyPreferencesConfig.SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    var isLogin: Boolean
        get() = sharedPreferences.getBoolean(MyPreferencesConfig.IS_LOGIN, false)
        set(value) = editor.putBoolean(MyPreferencesConfig.IS_LOGIN, value).apply()

    var userUid: String?
        get() = sharedPreferences.getString(MyPreferencesConfig.USER_UID, "Uid not found")
        set(value) = editor.putString(MyPreferencesConfig.USER_UID, value).apply()
}