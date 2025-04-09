package uk.ac.tees.mad.moodlog.model.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.moodlog.model.dataclass.firebase.FirestoreResult
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData

class JournalFirestoreRepository(
    private val firestore: FirebaseFirestore
) {

    private fun getJournalCollection(userId: String) =
        firestore.collection("users").document(userId).collection("journals")

    fun addJournalEntry(userId: String, journalEntry: LocalJournalData): Flow<FirestoreResult<String>> = flow {
        emit(FirestoreResult.Loading)
        try {
            // Convert LocalJournalData to a Map (Firestore document)
            val journalMap = journalEntry.toMapForFirestore()

            // Add the journal entry to Firestore
            val documentReference = getJournalCollection(userId).add(journalMap).await()

            val firestoreId = documentReference.id
            emit(FirestoreResult.Success(firestoreId))
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun getJournalEntries(userId: String): Flow<FirestoreResult<List<LocalJournalData>>> = flow {
        emit(FirestoreResult.Loading)
        try {
            val querySnapshot = getJournalCollection(userId).orderBy("journalDate", Query.Direction.ASCENDING).get().await()
            val journalEntries = querySnapshot.documents.mapNotNull { document ->
                val journalEntry = document.toObject(LocalJournalData::class.java)
                journalEntry?.copy(firestoreId = document.id) // set firestoreId
            }
            emit(FirestoreResult.Success(journalEntries))
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun updateJournalEntry(userId: String, journalEntry: LocalJournalData): Flow<FirestoreResult<Boolean>> = flow {
        emit(FirestoreResult.Loading)
        try {
            // Find the correct Firestore document ID
            val firestoreId = journalEntry.firestoreId
            if(firestoreId.isNotBlank()){
                // Update the journal entry
                getJournalCollection(userId).document(firestoreId).set(journalEntry.toMapForFirestore()).await()
                emit(FirestoreResult.Success(true))
            }else{
                emit(FirestoreResult.Error(Exception("Can not find journal in the database")))
            }

        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

        fun updateFirestoreJournalEntry(userId: String, journalEntry: LocalJournalData): Flow<FirestoreResult<Boolean>> = flow {
        emit(FirestoreResult.Loading)
        try {
            // Find the correct Firestore document ID
            val firestoreId = journalEntry.firestoreId
            if (firestoreId.isNotBlank()) {
                // Update the journal entry, excluding isDeleted and needsUpdate
                getJournalCollection(userId).document(firestoreId).update(journalEntry.toMapForFirestore()).await()
                emit(FirestoreResult.Success(true))
            } else {
                emit(FirestoreResult.Error(Exception("Firestore ID is missing for journal entry.")))
            }
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }


    fun deleteJournalEntry(userId: String, journalEntry: LocalJournalData): Flow<FirestoreResult<Boolean>> = flow {
        emit(FirestoreResult.Loading)
        try {
            // Find the correct Firestore document ID
            val firestoreId = journalEntry.firestoreId
            getJournalCollection(userId).document(firestoreId).delete().await()
            emit(FirestoreResult.Success(true))
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }
}

// Extension function to convert LocalJournalData to a Map for Firestore
fun LocalJournalData.toMapForFirestore(): Map<String, Any?> {
    return mapOf(
        "userId" to userId,
        "journalContent" to journalContent,
        "journalDate" to journalDate,
        "journalTime" to journalTime,
        "journalMood" to journalMood,
        "journalLocationLatitude" to journalLocationLatitude,
        "journalLocationLongitude" to journalLocationLongitude,
        "journalLocationAddress" to journalLocationAddress,
        "journalImage" to journalImage,
    )
}