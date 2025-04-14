package uk.ac.tees.mad.moodlog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.moodlog.model.dataclass.firebase.AuthResult
import uk.ac.tees.mad.moodlog.model.dataclass.firebase.UserData
import uk.ac.tees.mad.moodlog.model.dataclass.firebase.UserDetails
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository
import uk.ac.tees.mad.moodlog.model.repository.NetworkRepository

class ProfileScreenViewModel(
    private val authRepository: AuthRepository,
    private val networkRepository: NetworkRepository,
) : ViewModel() {
    val isNetworkAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _offlineMode = MutableStateFlow<Boolean?>(null)
    val offlineMode: StateFlow<Boolean?> = _offlineMode.asStateFlow()

    private val _userDetails = MutableStateFlow<AuthResult<UserDetails>>(AuthResult.Loading)
    val userDetails: StateFlow<AuthResult<UserDetails>> = _userDetails.asStateFlow()

    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    private val _updateNameResult = MutableStateFlow<AuthResult<Boolean>>(AuthResult.Success(false))
    val updateNameResult: StateFlow<AuthResult<Boolean>> = _updateNameResult.asStateFlow()

    init {
        observeNetworkConnectivity()
        fetchUserDetails()
    }

    private fun observeNetworkConnectivity() {
        viewModelScope.launch {
            networkRepository.isNetworkAvailable.collect { isAvailable ->
                isNetworkAvailable.value = isAvailable
                if (isAvailable) {
                    _offlineMode.value = false
                    println("Internet is available")
                } else {
                    _offlineMode.value = true
                    println("Internet is not available")
                }
            }
        }
    }

    fun fetchUserDetails() {
        viewModelScope.launch {
            authRepository.getCurrentUserDetails().collect { result ->
                _userDetails.value = result
                if (result is AuthResult.Success) {
                    _userData.update {
                        it.copy(
                            userDetails = result.data, userId = authRepository.getCurrentUserId()
                        )
                    }
                }
            }
        }
    }

    fun logOut() {
        authRepository.signOut()
    }

    fun updateDisplayName(displayName: String) {
        viewModelScope.launch {
            authRepository.updateDisplayName(displayName).collect { result ->
                _updateNameResult.value = result
                if (result is AuthResult.Success) {
                    fetchUserDetails()
                }
            }
        }
    }
}