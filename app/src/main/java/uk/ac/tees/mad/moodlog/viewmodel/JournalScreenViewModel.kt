package uk.ac.tees.mad.moodlog.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.moodlog.model.dataclass.location.LocationData
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository
import uk.ac.tees.mad.moodlog.model.repository.LocalJournalDataRepository

class JournalScreenViewModel
    (
    private val authRepository: AuthRepository,
    private val localJournalDataRepository: LocalJournalDataRepository
) : ViewModel() {

    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _sliderPosition = MutableStateFlow(2f)
    val sliderPosition: StateFlow<Float> = _sliderPosition.asStateFlow()

    private val _mood = MutableStateFlow("Neutral")
    val mood: StateFlow<String> = _mood.asStateFlow()

    private val _journalContent = MutableStateFlow("")
    val journalContent: StateFlow<String> = _journalContent.asStateFlow()

    private val _journalData = MutableStateFlow<List<LocalJournalData>>(emptyList())
    val journalData: StateFlow<List<LocalJournalData>> = _journalData.asStateFlow()

    init{
        getJournalDataForUser(authRepository.getCurrentUserId().toString())
    }

    fun updateJournalContent(newContent: String) {
        _journalContent.value = newContent
    }

    fun updateSliderPosition(newPosition: Float) {
        _sliderPosition.value = newPosition
        updateMood(
            when (sliderPosition.value) {
                0f -> "Very Dissatisfied"
                1f -> "Dissatisfied"
                2f -> "Neutral"
                3f -> "Satisfied"
                4f -> "Very Satisfied"
                else -> "Mood"
            }
        )
    }

    fun updateMood(newMood: String) {
        _mood.value = newMood
    }

    fun updateSelectedDate(newDate: String) {
        _selectedDate.value = newDate
    }

    fun updateLocation(newLocation: LocationData) {
        _location.value = newLocation
    }

    fun logOut() {
        authRepository.signOut()
    }

    fun saveJournal() {
        viewModelScope.launch {
            val journalData = LocalJournalData(
                userId = authRepository.getCurrentUserId().toString(),
                journalContent = journalContent.value,
                journalDate = selectedDate.value,
                journalTime = "00:00",
                journalMood = mood.value,
                journalLocationLatitude = location.value?.latitude ?: 0.0,
                journalLocationLongitude = location.value?.longitude ?: 0.0,
                journalLocationAddress = "",
                journalImage = "",
                isSynced = false
            )
            localJournalDataRepository.insertJournalData(journalData)
        }
    }

    fun getJournalDataForUser(userId: String): List<LocalJournalData> {
        viewModelScope.launch {
            _journalData.value = localJournalDataRepository.getAllJournalDataForUser(userId)
        }
        return journalData.value
    }

    fun deleteJournalData(journalDataId: Int) {
        viewModelScope.launch {
            localJournalDataRepository.deleteJournalDataById(journalDataId)
        }
    }
}