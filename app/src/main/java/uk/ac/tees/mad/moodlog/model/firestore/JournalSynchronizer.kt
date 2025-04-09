package uk.ac.tees.mad.moodlog.model.firestore

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.ac.tees.mad.moodlog.model.dataclass.firebase.FirestoreResult
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository
import uk.ac.tees.mad.moodlog.model.repository.JournalFirestoreRepository
import uk.ac.tees.mad.moodlog.model.repository.LocalJournalDataRepository

class JournalSynchronizer(
    private val localJournalDataRepository: LocalJournalDataRepository,
    private val journalFirestoreRepository: JournalFirestoreRepository,
    private val authRepository: AuthRepository,
) {
    fun startSync() {
        Log.d("JournalSync", "JournalSynchronizer started")
        CoroutineScope(Dispatchers.IO).launch {
            localJournalDataRepository.getJournalsForSync().collectLatest { journals ->
                Log.d("JournalSync", "Found ${journals.size} journals to sync")
                journals.forEach { journal ->
                    Log.d("JournalSync", "Processing journal: $journal")
                    val userId = authRepository.getCurrentUserId()
                    if (userId != null) {
                        Log.d("JournalSync", "User ID: $userId")
                        if (journal.firestoreId.isBlank() && !journal.isDeleted) { // New journal
                            Log.d("JournalSync", "Adding new journal to Firestore")
                            journalFirestoreRepository.addJournalEntry(userId, journal)
                                .collectLatest { firestoreResult ->
                                    when (firestoreResult) {
                                        is FirestoreResult.Success -> {
                                            Log.d("JournalSync", "Successfully added journal, firestoreId: ${firestoreResult.data}") // Added log
                                            // Update the local journal with the firestoreId
                                            localJournalDataRepository.updateFirestoreId(
                                                journal.id,
                                                firestoreResult.data
                                            )
                                        }

                                        is FirestoreResult.Error -> {
                                            // Handle error - could retry, log, etc.
                                            Log.e("JournalSync", "Error adding journal to Firestore", firestoreResult.exception) // Added log
                                        }

                                        else -> {}
                                    }
                                }
                        } else if (journal.needsUpdate) {
                            if(!journal.isDeleted){
                                Log.d("JournalSync", "Updating journal in Firestore")
                                journalFirestoreRepository.updateFirestoreJournalEntry(userId, journal)
                                    .collectLatest { firestoreResult ->
                                        if (firestoreResult is FirestoreResult.Success) {
                                            Log.d("JournalSync", "Successfully updated journal in Firestore")
                                            // Clear needsUpdate
                                            localJournalDataRepository.updateJournalData(journal.copy(needsUpdate = false))
                                        }
                                    }
                            }
                        } else if(journal.isDeleted){
                            Log.d("JournalSync", "Deleting journal from Firestore")
                            journalFirestoreRepository.deleteJournalEntry(userId, journal)
                                .collectLatest { firestoreResult ->
                                    if (firestoreResult is FirestoreResult.Success) {
                                        Log.d("JournalSync", "Successfully deleted journal from Firestore")
                                        // Delete from local db
                                        localJournalDataRepository.deleteJournalData(journal)
                                    }
                                }
                        }
                    }else{
                        Log.e("JournalSync", "User not logged in")
                    }
                }
            }
        }
    }
}