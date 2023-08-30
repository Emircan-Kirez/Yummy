package com.emircankirez.yummy.ui.presentation.favorite


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.data.local.sharedPreferences.MyPreferences
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.domain.model.Meal
import com.emircankirez.yummy.domain.model.User
import com.emircankirez.yummy.domain.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private var _userResponse = MutableStateFlow<Resource<User>>(Resource.Empty)
    val userResponse : StateFlow<Resource<User>> = _userResponse

    private var _favoritesResponse = MutableStateFlow<Resource<List<Meal>>>(Resource.Empty)
    val favoritesResponse : StateFlow<Resource<List<Meal>>> = _favoritesResponse

    init {
        getUserInformation()
        getAllFavorites()
    }

    private fun getUserInformation() = viewModelScope.launch {
        firebaseRepository.getUserInformation().collect{ result ->
            when(result){
                Resource.Empty -> {}
                is Resource.Error -> {
                    _userResponse.value = Resource.Error(result.message)
                }
                Resource.Loading -> {
                    _userResponse.value = Resource.Loading
                }
                is Resource.Success -> {
                    _userResponse.value = Resource.Success(result.data)
                }
            }
        }
    }

    private fun getAllFavorites() = viewModelScope.launch {
        firebaseRepository.getAllFavorites().collect{ result ->
            when(result){
                Resource.Empty -> {}
                is Resource.Error -> {
                    _favoritesResponse.value = Resource.Error(result.message)
                }
                Resource.Loading -> {
                    _favoritesResponse.value = Resource.Loading
                }
                is Resource.Success -> {
                    _favoritesResponse.value = Resource.Success(result.data)
                }
            }
        }
    }
}