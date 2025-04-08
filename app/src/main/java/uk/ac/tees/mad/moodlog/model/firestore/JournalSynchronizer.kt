package uk.ac.tees.mad.moodlog.model.firestore

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
        CoroutineScope(Dispatchers.IO).launch {
            localJournalDataRepository.getAllUnsyncedJournals().collectLatest { unsyncedJournals ->
                unsyncedJournals.forEach { journal ->
                    val userId = authRepository.getCurrentUserId()
                    if (userId != null) {
                        journalFirestoreRepository.addJournalEntry(userId, journal).collectLatest { firestoreResult ->
                            if(firestoreResult is FirestoreResult.Success){
                                localJournalDataRepository.markAsSynced(journal.id)
                            }
                        }
                    }
                }
            }
        }
    }
}