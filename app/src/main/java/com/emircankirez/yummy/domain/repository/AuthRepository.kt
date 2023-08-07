package com.emircankirez.yummy.domain.repository

import com.emircankirez.yummy.common.Resource
import com.google.firebase.auth.AuthResult

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun firebaseRegister(
        email: String,
        password: String
    ) : Flow<Resource<AuthResult>>

    suspend fun firebaseLogin(
        email: String,
        password: String
    ) : Flow<Resource<AuthResult>>

    suspend fun firebaseSendPasswordResetLink(
        email: String
    ) : Flow<Resource<Void>>

}