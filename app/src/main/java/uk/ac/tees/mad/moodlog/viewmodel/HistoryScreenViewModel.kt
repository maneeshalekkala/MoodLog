package uk.ac.tees.mad.moodlog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.ac.tees.mad.moodlog.model.dataclass.firebase.FirestoreResult
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository
import uk.ac.tees.mad.moodlog.model.repository.JournalFirestoreRepository
import uk.ac.tees.mad.moodlog.model.repository.LocalJournalDataRepository

class HistoryScreenViewModel(
    private val authRepository: AuthRepository,
    private val localJournalDataRepository: LocalJournalDataRepository,
    private val journalFirestoreRepository: JournalFirestoreRepository,
) : ViewModel() {

    private val _journalData = MutableStateFlow<List<LocalJournalData>>(emptyList())
    val journalData: StateFlow<List<LocalJournalData>> = _journalData.asStateFlow()

    init {
        startLoadingJournalData()
    }

    fun startLoadingJournalData(){
        getJournalDataForUser(authRepository.getCurrentUserId().toString())
    }

    fun getJournalDataForUser(userId: String) {
        viewModelScope.launch {
            localJournalDataRepository.getAllJournalDataForUser(userId).collectLatest { journalList ->
                _journalData.value = journalList
            }
        }
    }

    fun deleteJournalData(journalData: LocalJournalData) {
        viewModelScope.launch {
            //soft delete
            localJournalDataRepository.updateJournalData(journalData.copy(isDeleted = true))
        }
    }
}

//    fun getJournalDataForUser(userId: String) {
//        viewModelScope.launch {
//            journalFirestoreRepository.getJournalEntries(userId).collectLatest { firestoreResult ->
//                when (firestoreResult) {
//                    is FirestoreResult.Success -> {
//                        _journalData.value = firestoreResult.data
//                    }
//
//                    is FirestoreResult.Error -> {
//                        // Handle error - could retry, log, etc.
//                        Log.e(
//                            "JournalSync",
//                            "Error getting journal from Firestore",
//                            firestoreResult.exception
//                        ) // Added log
//                    }
//
//                    else -> {}
//                }
//            }
//        }
//    }
//}