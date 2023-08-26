package com.emircankirez.yummy.ui.presentation.userEdit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.domain.model.User
import com.emircankirez.yummy.domain.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserEditViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val resourceProvider: ResourceProvider
): ViewModel() {

    private var _saveUserInformationResponse = MutableStateFlow<Resource<Unit>>(Resource.Empty)
    val saveUserInformationResponse : StateFlow<Resource<Unit>> = _saveUserInformationResponse

    fun saveUserInformation(uri: Uri?, user: User) = viewModelScope.launch {
        if(user.name.isNullOrBlank() || user.surname.isNullOrBlank()){
            _saveUserInformationResponse.value = Resource.Error(resourceProvider.getString(R.string.empyt_name_or_surname_field))
        }else{
            firebaseRepository.saveUserInformation(uri, user).collect{ result ->
                when(result){
                    Resource.Empty -> {}
                    is Resource.Error -> {
                        _saveUserInformationResponse.value = Resource.Error(result.message)
                    }
                    Resource.Loading -> {
                        _saveUserInformationResponse.value = Resource.Loading
                    }
                    is Resource.Success -> {
                        _saveUserInformationResponse.value = Resource.Success(result.data)
                    }
                }
            }
        }
    }
}