package com.emircankirez.yummy.ui.presentation.auth.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.common.extensions.isValidEmail
import com.emircankirez.yummy.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

    fun register(context: Context, email: String, password: String) = viewModelScope.launch {
        if(email.isNotBlank() && password.isNotBlank()){
            if(email.isValidEmail()){
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
            }else{
                _registerResponse.value = Resource.Error(context.getString(R.string.not_valid_email))
            }
        }else{
            _registerResponse.value = Resource.Error(context.getString(R.string.empty_email_or_password_field))
        }
    }

    fun resetRegisterResponse(){
        _registerResponse.value = Resource.Empty
    }
}