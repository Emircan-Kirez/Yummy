package com.emircankirez.yummy.di

import com.emircankirez.yummy.data.local.sharedPreferences.MyPreferences
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.data.remote.RecipeApi
import com.emircankirez.yummy.data.repository.AuthRepositoryImpl
import com.emircankirez.yummy.data.repository.FirebaseRepositoryImpl
import com.emircankirez.yummy.data.repository.RecipeRepositoryImpl
import com.emircankirez.yummy.domain.repository.AuthRepository
import com.emircankirez.yummy.domain.repository.FirebaseRepository
import com.emircankirez.yummy.domain.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(resourceProvider: ResourceProvider, myPreferences: MyPreferences) : AuthRepository {
        return AuthRepositoryImpl(resourceProvider, myPreferences)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(api: RecipeApi, resourceProvider: ResourceProvider) : RecipeRepository {
        return RecipeRepositoryImpl(api, resourceProvider)
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(resourceProvider: ResourceProvider, myPreferences: MyPreferences) : FirebaseRepository {
        return FirebaseRepositoryImpl(resourceProvider, myPreferences)
    }
}