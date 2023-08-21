package com.emircankirez.yummy.data.repository

import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.Constants.USERS
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.domain.model.User
import com.emircankirez.yummy.domain.repository.FirebaseRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val resourceProvider: ResourceProvider
) : FirebaseRepository {

    private val userRef = Firebase.firestore.collection(USERS)
    override suspend fun getUserInformation(uid: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try{
            val doc = userRef.document(uid).get().await()
            val user = User(
                uid = doc.getString("uid"),
                email = doc.getString("email"),
                name = doc.getString("name"),
                surname = doc.getString("surname"),
                photoUrl = doc.getString("photoUrl")
            )
            emit(Resource.Success(user))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: resourceProvider.getString(R.string.unknown_error_for_getting_user_information)))
        }
    }
}