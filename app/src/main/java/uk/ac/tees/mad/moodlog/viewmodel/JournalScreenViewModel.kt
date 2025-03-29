package uk.ac.tees.mad.moodlog.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import uk.ac.tees.mad.moodlog.model.dataclass.location.LocationData
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository

class JournalScreenViewModel
    (
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    fun updateLocation(newLocation: LocationData) {
        _location.value = newLocation
    }

    fun logOut() {
        authRepository.signOut()
    }
}