package com.emircankirez.yummy.ui.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private var _registerResponse = MutableStateFlow<Resource<AuthResult>>(Resource.Empty)
    val registerResponse : StateFlow<Resource<AuthResult>> = _registerResponse

    fun register(email: String, password: String) = viewModelScope.launch {
        authRepository.firebaseRegister(email, password).collect{ result ->
            when(result){
                is Resource.Error -> {
                    _registerResponse.value = Resource.Error(result.message)
                }
                Resource.Loading -> {
                    _registerResponse.value = Resource.Loading
                }
                is Resource.Success -> {
                    _registerResponse.value = Resource.Success(result.data)
                }
                Resource.Empty -> {}
            }
        }
    }

    fun setRegisterResponseError(message: String){
        _registerResponse.value = Resource.Error(message)
    }
}