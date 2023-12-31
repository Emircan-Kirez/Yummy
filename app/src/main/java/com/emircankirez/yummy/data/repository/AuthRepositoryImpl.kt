package com.emircankirez.yummy.data.repository

import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.Constants.USERS
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.data.local.sharedPreferences.MyPreferences
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.domain.model.User
import com.emircankirez.yummy.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.Exception

class AuthRepositoryImpl @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val myPreferences: MyPreferences
) : AuthRepository {

    private val auth = Firebase.auth
    private val userRef = Firebase.firestore.collection(USERS)
    override suspend fun firebaseRegister(
        email: String,
        password: String,
        name: String,
        surname: String
    ): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading)
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            val uid = result.user?.uid!!
            val user = User(uid, email, name, surname)
            userRef.document(uid).set(user).await()

            myPreferences.isLogin = true
            myPreferences.userUid = uid

            emit(Resource.Success(result))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: resourceProvider.getString(R.string.unknown_error_for_firebase_register)))
        }
    }
    override suspend fun firebaseLogin(
        email: String,
        password: String
    ): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading)
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()

            myPreferences.isLogin = true
            myPreferences.userUid = result.user?.uid!!

            emit(Resource.Success(result))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: resourceProvider.getString(R.string.unknown_error_for_firebase_login)))
        }
    }

    override suspend fun firebaseSendPasswordResetLink(email: String): Flow<Resource<Void>> = flow {
        emit(Resource.Loading)
        try{
            val result = auth.sendPasswordResetEmail(email).await()
            emit(Resource.Success(result))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: resourceProvider.getString(R.string.unknown_error_for_firebase_send_password_reset_email)))
        }
    }
}