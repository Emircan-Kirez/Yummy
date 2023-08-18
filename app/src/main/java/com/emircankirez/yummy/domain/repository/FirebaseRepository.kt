package com.emircankirez.yummy.domain.repository

import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    suspend fun getUserInformation(uid: String) : Flow<Resource<User>>
}