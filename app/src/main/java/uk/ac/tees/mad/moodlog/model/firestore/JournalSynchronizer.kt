package uk.ac.tees.mad.moodlog.model.firestore

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
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
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                syncFromFirestoreToLocal(userId)
                localJournalDataRepository.getJournalsForSync().collectLatest { journals ->
                    Log.d("JournalSync", "Found ${journals.size} journals to sync")
                    journals.forEach { journal ->
                        Log.d("JournalSync", "Processing journal: $journal")

                        if (journal.firestoreId.isBlank() && !journal.isDeleted) { // New journal
                            Log.d("JournalSync", "Adding new journal to Firestore")
                            journalFirestoreRepository.addJournalEntry(userId, journal)
                                .collectLatest { firestoreResult ->
                                    when (firestoreResult) {
                                        is FirestoreResult.Success -> {
                                            Log.d(
                                                "JournalSync",
                                                "Successfully added journal, firestoreId: ${firestoreResult.data}"
                                            ) // Added log
                                            // Update the local journal with the firestoreId
                                            localJournalDataRepository.updateFirestoreId(
                                                journal.id, firestoreResult.data
                                            )
                                        }

                                        is FirestoreResult.Error -> {
                                            // Handle error - could retry, log, etc.
                                            Log.e(
                                                "JournalSync",
                                                "Error adding journal to Firestore",
                                                firestoreResult.exception
                                            ) // Added log
                                        }

                                        else -> {}
                                    }
                                }
                        } else if (journal.needsUpdate) {
                            if (!journal.isDeleted) {
                                Log.d("JournalSync", "Updating journal in Firestore")
                                journalFirestoreRepository.updateFirestoreJournalEntry(
                                    userId,
                                    journal
                                ).collectLatest { firestoreResult ->
                                        if (firestoreResult is FirestoreResult.Success) {
                                            Log.d(
                                                "JournalSync",
                                                "Successfully updated journal in Firestore"
                                            )
                                            // Clear needsUpdate
                                            localJournalDataRepository.updateJournalData(
                                                journal.copy(
                                                    needsUpdate = false
                                                )
                                            )
                                        }
                                    }
                            }
                        } else if (journal.isDeleted) {
                            Log.d("JournalSync", "Deleting journal from Firestore")
                            journalFirestoreRepository.deleteJournalEntry(userId, journal)
                                .collectLatest { firestoreResult ->
                                    if (firestoreResult is FirestoreResult.Success) {
                                        Log.d(
                                            "JournalSync",
                                            "Successfully deleted journal from Firestore"
                                        )
                                        // Delete from local db
                                        localJournalDataRepository.deleteJournalData(journal)
                                    }
                                }
                        }
                    }
                }
            } else {
                Log.e("JournalSync", "User not logged in")
            }

        }
    }

    private suspend fun syncFromFirestoreToLocal(userId: String) {
        Log.d("JournalSync", "Syncing Firestore to Local DB for user: $userId")
        val localJournals =
            localJournalDataRepository.getAllJournalDataForUser(userId).firstOrNull() ?: emptyList()
        journalFirestoreRepository.getJournalEntries(userId).collectLatest { firestoreResult ->
            when (firestoreResult) {
                is FirestoreResult.Success -> {
                    val firestoreJournals = firestoreResult.data
                    firestoreJournals.forEach { firestoreJournal ->
                        // Check if the journal already exists locally based on firestoreId
                        val localJournal =
                            localJournals.find { it.firestoreId == firestoreJournal.firestoreId }
                        if (localJournal == null) {
                            // Insert the new journal into the local database
                            Log.d(
                                "JournalSync",
                                "Adding new journal from Firestore to local DB: ${firestoreJournal.firestoreId}"
                            )
                            localJournalDataRepository.insertJournalData(firestoreJournal)
                        } else if (firestoreJournal != localJournal) {
                            localJournalDataRepository.updateJournalData(firestoreJournal)
                            Log.d(
                                "JournalSync",
                                "Updating journal from Firestore to local DB: ${firestoreJournal.firestoreId}"
                            )
                        }
                    }
                }

                is FirestoreResult.Error -> {
                    Log.e(
                        "JournalSync",
                        "Error fetching journals from Firestore",
                        firestoreResult.exception
                    )
                }

                else -> {}
            }
        }
    }
}