package com.emircankirez.yummy.di

import android.content.Context
import com.emircankirez.yummy.common.Constants.BASE_URL
import com.emircankirez.yummy.data.local.sharedPreferences.MyPreferences
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.data.remote.RecipeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit) : RecipeApi {
        return retrofit.create(RecipeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMyPreferences(@ApplicationContext context: Context, resourceProvider: ResourceProvider) : MyPreferences {
        return MyPreferences(context, resourceProvider)
    }

    @Provides
    @Singleton
    fun provideResourceProvider(@ApplicationContext context: Context) : ResourceProvider {
        return ResourceProvider(context)
    }
}