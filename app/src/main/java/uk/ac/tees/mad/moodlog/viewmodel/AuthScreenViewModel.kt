package uk.ac.tees.mad.moodlog.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.HowToReg
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository

class AuthScreenViewModel(
    //private val authRepository: AuthRepository
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
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun switchTabState() {
        _tabState.value = if (_tabState.value == 0) 1 else 0
        updateEmail("")
        updatePassword("")
    }
}