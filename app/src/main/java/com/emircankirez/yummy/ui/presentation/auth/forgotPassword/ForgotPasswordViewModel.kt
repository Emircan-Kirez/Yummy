package com.emircankirez.yummy.ui.presentation.auth.forgotPassword

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.common.extensions.isValidEmail
import com.emircankirez.yummy.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _forgotPasswordResponse = MutableStateFlow<Resource<Void>>(Resource.Empty)
    val forgotPasswordResponse : StateFlow<Resource<Void>> = _forgotPasswordResponse
    fun sendPasswordResetLink(context: Context, email: String) = viewModelScope.launch {
        if(email.isNotBlank()){
            if(email.isValidEmail()){
                authRepository.firebaseSendPasswordResetLink(email).collect{ result ->
                    when(result){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            _forgotPasswordResponse.value = Resource.Error(result.message)
                        }
                        Resource.Loading -> {
                            _forgotPasswordResponse.value = Resource.Loading
                        }
                        is Resource.Success -> {
                            _forgotPasswordResponse.value = Resource.Success(result.data)
                        }
                    }
                }
            }else{
                _forgotPasswordResponse.value = Resource.Error(context.getString(R.string.not_valid_email))
            }
        }else{
            _forgotPasswordResponse.value = Resource.Error(context.getString(R.string.empty_email_field))
        }
    }

    fun resetForgotPasswordResponse(){
        _forgotPasswordResponse.value = Resource.Empty
    }
}