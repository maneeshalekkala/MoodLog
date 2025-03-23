package uk.ac.tees.mad.moodlog.viewmodel

import androidx.lifecycle.ViewModel
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository

class JournalScreenViewModel
    (
    private val authRepository: AuthRepository
) : ViewModel() {
    fun logOut() {
        authRepository.signOut()
    }
}