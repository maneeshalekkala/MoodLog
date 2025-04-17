package uk.ac.tees.mad.moodlog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.ac.tees.mad.moodlog.model.dataclass.state.LoadingState
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository
import uk.ac.tees.mad.moodlog.model.repository.NetworkRepository

class SplashScreenViewModel(
    private val networkRepository: NetworkRepository, private val authRepository: AuthRepository
) : ViewModel() {
    private val _isNetworkAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState<Any>>(LoadingState.Loading)
    val loadingState: StateFlow<LoadingState<Any>> = _loadingState.asStateFlow()
    private var permissionsGranted = false

    private fun observeNetworkConnectivity() {
        viewModelScope.launch {
            networkRepository.isNetworkAvailable.collect { isAvailable ->
                _isNetworkAvailable.value = isAvailable
                if (isAvailable) {
                    println("Internet is available")
                } else {
                    println("Internet is not available")
                }
            }
        }
    }

    init {
        observeNetworkConnectivity()
        startLoading()
    }

    fun startLoading() {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            if (!permissionsGranted) {
                return@launch
            }
            networkRepository.isNetworkAvailable.collectLatest { isAvailable ->
                if (isAvailable) {
                    _loadingState.value = LoadingState.Loading
                    delay(5000)
                    _loadingState.value = LoadingState.Success(Any())
                } else {
                    _loadingState.value = LoadingState.Loading
                    delay(5000)
                    val message = "No internet connection"
                    if (authRepository.isSignedIn()) {
                        // Load user data from firebase
                        // if loading done, put success
                        _loadingState.value = LoadingState.Success(Any())

                    } else {
                        _loadingState.value = LoadingState.Error(message)
                    }
                }
            }
        }
    }

    fun onPermissionsGranted() {
        permissionsGranted = true
        startLoading()
    }

    fun onPermissionsDenied() {
        permissionsGranted = false
        _loadingState.value =
            LoadingState.Error("Permissions Denied, Grant Permission from settings")
    }

    fun isSignedIn(): Boolean {
        return authRepository.isSignedIn()
    }
}