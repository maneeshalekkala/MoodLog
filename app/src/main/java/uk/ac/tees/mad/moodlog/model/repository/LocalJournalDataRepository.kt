package uk.ac.tees.mad.moodlog.model.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData
import uk.ac.tees.mad.moodlog.model.room.LocalJournalDataDao
import kotlin.text.insert

class LocalJournalDataRepository(private val localJournalDataDao: LocalJournalDataDao) {

    suspend fun insertJournalData(journalData: LocalJournalData): Long {
        return localJournalDataDao.insert(journalData)
    }

    suspend fun upsertJournalData(journalData: LocalJournalData): Long {
        return localJournalDataDao.upsert(journalData)
    }

    suspend fun deleteJournalData(journalData: LocalJournalData): Int {
        return localJournalDataDao.delete(journalData)
    }

    suspend fun getAllJournalDataForUser(userId: String): List<LocalJournalData> {
        return localJournalDataDao.getAllJournalDataForUser(userId)
    }

    suspend fun getJournalDataById(id: Int): LocalJournalData? {
        return localJournalDataDao.getJournalDataById(id)
    }

    fun getAllUnsyncedJournals(): Flow<List<LocalJournalData>> {
        return localJournalDataDao.getUnsyncedJournals()
    }

    suspend fun markAsSynced(id: Int): Int {
        return localJournalDataDao.updateSyncStatus(id)
    }

    suspend fun deleteAllJournalDataForUser(userId: String): Int {
        return localJournalDataDao.deleteAllJournalDataForUser(userId)
    }

    suspend fun deleteJournalDataById(id: Int): Int {
        return localJournalDataDao.deleteJournalDataById(id)
    }

    suspend fun updateJournalData(journalData: LocalJournalData): Int {
        return localJournalDataDao.updateJournalData(
            journalData.id,
            journalData.journalContent,
            journalData.journalDate,
            journalData.journalTime,
            journalData.journalMood,
            journalData.journalLocationLatitude,
            journalData.journalLocationLongitude,
            journalData.journalLocationAddress,
            journalData.journalImage,
            journalData.isSynced
        )
    }
}