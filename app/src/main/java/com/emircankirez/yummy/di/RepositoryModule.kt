package com.emircankirez.yummy.di

import android.content.Context
import com.emircankirez.yummy.data.remote.RecipeApi
import com.emircankirez.yummy.data.repository.AuthRepositoryImpl
import com.emircankirez.yummy.data.repository.RecipeRepositoryImpl
import com.emircankirez.yummy.domain.repository.AuthRepository
import com.emircankirez.yummy.domain.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(@ApplicationContext context: Context) : AuthRepository {
        return AuthRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(api: RecipeApi) : RecipeRepository {
        return RecipeRepositoryImpl(api)
    }
}