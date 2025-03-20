package uk.ac.tees.mad.moodlog.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.HowToReg
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uk.ac.tees.mad.moodlog.model.dataclass.firebase.AuthResult
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository

class AuthScreenViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _tabState = MutableStateFlow(0)
    val tabState = _tabState.asStateFlow()

    val titlesAndIcons = listOf(
        "Sign In" to Icons.AutoMirrored.Filled.Login,
        "Register" to Icons.Filled.HowToReg,
    )

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val _signInResult = MutableStateFlow<AuthResult<Boolean>>(AuthResult.Success(false))
    val signInResult: StateFlow<AuthResult<Boolean>> = _signInResult.asStateFlow()

    private val _registerResult = MutableStateFlow<AuthResult<Boolean>>(AuthResult.Success(false))
    val registerResult: StateFlow<AuthResult<Boolean>> = _registerResult.asStateFlow()

    private val _isSignInMode = MutableStateFlow(false)
    val isSignInMode = _isSignInMode.asStateFlow()

    private val _registerMode = MutableStateFlow(false)
    val registerMode = _registerMode.asStateFlow()


    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateConfirmPassword(newPassword: String) {
        _confirmPassword.value = newPassword
    }

    fun updateTabState(newState: Int) {
        _tabState.value = newState
        updateEmail("")
        updatePassword("")
        updateConfirmPassword("")
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun switchTabState() {
        _tabState.value = if (_tabState.value == 0) 1 else 0
        updateEmail("")
        updatePassword("")
        updateConfirmPassword("")
    }

    fun signIn(email: String, pass: String) {
        _signInResult.value = AuthResult.Loading
        switchSignInMode()
        authRepository.signIn(email, pass).onEach { result ->
            _signInResult.value = result
        }.launchIn(viewModelScope)
    }

    fun switchSignInMode() {
        _isSignInMode.value = !_isSignInMode.value
    }


    fun register(email: String, pass: String) {
        _registerResult.value = AuthResult.Loading
        switchRegisterMode()
        authRepository.signUp(email, pass).onEach { result ->
            _registerResult.value = result
        }.launchIn(viewModelScope)
    }

    fun switchRegisterMode() {
        _registerMode.value = !_registerMode.value
    }
}