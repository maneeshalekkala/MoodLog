package uk.ac.tees.mad.moodlog.model.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData
import uk.ac.tees.mad.moodlog.model.room.LocalJournalDataDao
import kotlin.text.insert

class LocalJournalDataRepository(private val localJournalDataDao: LocalJournalDataDao) {

    suspend fun insertJournalData(journalData: LocalJournalData): Long {
        return localJournalDataDao.insert(journalData)
    }

    suspend fun upsertJournalData(journalData: LocalJournalData) {
        return localJournalDataDao.upsert(journalData)
    }

    suspend fun deleteJournalData(journalData: LocalJournalData) {
        return localJournalDataDao.delete(journalData)
    }

    fun getAllJournalDataForUser(userId: String): Flow<List<LocalJournalData>> {
        return localJournalDataDao.getAllJournalDataForUser(userId) // Return Flow
    }

    suspend fun getJournalDataById(id: Int): LocalJournalData? {
        return localJournalDataDao.getJournalDataById(id)
    }

    fun getJournalsForSync(): Flow<List<LocalJournalData>> {
        return localJournalDataDao.getJournalsForSync()
    }

    suspend fun updateFirestoreId(id: Int, firestoreId: String) {
        localJournalDataDao.updateFirestoreId(id, firestoreId)
    }

    suspend fun deleteAllJournalDataForUser(userId: String): Int {
        return localJournalDataDao.deleteAllJournalDataForUser(userId)
    }

    suspend fun deleteJournalDataById(id: Int): Int {
        return localJournalDataDao.deleteJournalDataById(id)
    }

    suspend fun updateJournalData(journalData: LocalJournalData) {
        localJournalDataDao.updateJournalData(
            journalData.id,
            journalData.journalContent,
            journalData.journalDate,
            journalData.journalTime,
            journalData.journalMood,
            journalData.journalLocationLatitude,
            journalData.journalLocationLongitude,
            journalData.journalLocationAddress,
            journalData.journalImage,
            journalData.needsUpdate,
            journalData.isDeleted
        )
    }
}