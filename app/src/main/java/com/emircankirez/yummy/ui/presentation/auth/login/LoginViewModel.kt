package com.emircankirez.yummy.ui.presentation.auth.login

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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _loginResponse = MutableStateFlow<Resource<AuthResult>>(Resource.Empty)
    val loginResponse : StateFlow<Resource<AuthResult>> = _loginResponse

    fun login(context: Context, email: String, password: String) = viewModelScope.launch {
        if(email.isNotBlank() && password.isNotBlank()){
            if(email.isValidEmail()){
                authRepository.firebaseLogin(email, password).collect{ result ->
                    when(result){
                        is Resource.Error -> {
                            _loginResponse.value = Resource.Error(result.message)
                        }
                        Resource.Loading -> {
                            _loginResponse.value = Resource.Loading
                        }
                        is Resource.Success -> {
                            _loginResponse.value = Resource.Success(result.data)
                        }
                        Resource.Empty -> {}
                    }
                }
            }else{
                _loginResponse.value = Resource.Error(context.getString(R.string.not_valid_email))
            }
        }else{
            _loginResponse.value = Resource.Error(context.getString(R.string.empty_email_or_password_field))
        }
    }

    fun resetLoginResponse(){
        _loginResponse.value = Resource.Empty
    }

}