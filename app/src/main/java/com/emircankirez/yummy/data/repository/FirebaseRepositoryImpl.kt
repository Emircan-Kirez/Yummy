package com.emircankirez.yummy.data.repository

import android.net.Uri
import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.Constants.FAVORITES
import com.emircankirez.yummy.common.Constants.FAVORITE_MEALS
import com.emircankirez.yummy.common.Constants.PROFILE_PHOTOS
import com.emircankirez.yummy.common.Constants.USERS
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.data.local.sharedPreferences.MyPreferences
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.domain.model.Meal
import com.emircankirez.yummy.domain.model.User
import com.emircankirez.yummy.domain.repository.FirebaseRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val myPreferences: MyPreferences
) : FirebaseRepository {

    private val userRef = Firebase.firestore.collection(USERS)
    private val favoriteMealsRef = Firebase.firestore.collection(FAVORITE_MEALS).document(myPreferences.userUid!!).collection(
        FAVORITES)
    private val photoRef = Firebase.storage.reference.child(PROFILE_PHOTOS)

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

    override suspend fun saveUserInformation(uri: Uri?, user: User): Flow<Resource<Unit>> = flow {
        val uid = user.uid!!
        emit(Resource.Loading)
        try {
            if(uri == null){ // eğer seçili resim yoksa sadece isim ve soyisim güncelle
                userRef.document(uid).update(
                    hashMapOf<String, Any>(
                        "name" to user.name!!,
                        "surname" to user.surname!!
                    )
                ).await()
                emit(Resource.Success(Unit))
            }else{
                val fileName = "profile_photo.jpg"
                val userPhotoRef = photoRef.child("$uid/$fileName")

                val uploadTask = userPhotoRef.putFile(uri).await()
                if(uploadTask.task.isSuccessful){
                    val downloadUrl = userPhotoRef.downloadUrl.await().toString()

                    userRef.document(uid).update(
                        hashMapOf<String, Any>(
                            "photoUrl" to downloadUrl,
                            "name" to user.name!!,
                            "surname" to user.surname!!
                        )
                    ).await()
                    emit(Resource.Success(Unit))
                }
            }
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: resourceProvider.getString(R.string.unknown_error_for_saving_user_information)))
        }
    }

    override suspend fun isFavorite(mealId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try{
            val ds = favoriteMealsRef.document(mealId).get().await()
            if (ds != null && ds.exists()){
                emit(Resource.Success(true))
            }else{
                emit(Resource.Success(false))
            }
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: resourceProvider.getString(R.string.unknown_error_for_is_favorite)))
        }
    }

    override suspend fun addFavorite(meal: Meal): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            favoriteMealsRef.document(meal.id).set(meal).await()
            emit(Resource.Success(Unit))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: resourceProvider.getString(R.string.unknown_error_for_adding_favorite_meal)))
        }
    }

    override suspend fun removeFavorite(mealId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            favoriteMealsRef.document(mealId).delete().await()
            emit(Resource.Success(Unit))
        }catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: resourceProvider.getString(R.string.unknown_error_for_removing_favorite_meal)))
        }
    }
}