package uk.ac.tees.mad.moodlog.model.repository

import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData
import uk.ac.tees.mad.moodlog.model.room.LocalJournalDataDao

class LocalJournalDataRepository(private val localJournalDataDao: LocalJournalDataDao) {

    suspend fun insertJournalData(journalData: LocalJournalData) {
        localJournalDataDao.insert(journalData)
    }

    suspend fun upsertJournalData(journalData: LocalJournalData){
        localJournalDataDao.upsert(journalData)
    }

    suspend fun deleteJournalData(journalData: LocalJournalData) {
        localJournalDataDao.delete(journalData)
    }

    suspend fun getAllJournalDataForUser(userId: String): List<LocalJournalData> {
        return localJournalDataDao.getAllJournalDataForUser(userId)
    }

    suspend fun getJournalDataById(id: Int): LocalJournalData? {
        return localJournalDataDao.getJournalDataById(id)
    }

    suspend fun getAllUnsyncedJournalData(): List<LocalJournalData> {
        return localJournalDataDao.getAllUnsyncedJournalData()
    }

    suspend fun updateSyncStatus(id: Int) {
        localJournalDataDao.updateSyncStatus(id)
    }

    suspend fun deleteAllJournalDataForUser(userId: String) {
        localJournalDataDao.deleteAllJournalDataForUser(userId)
    }

    suspend fun deleteJournalDataById(id: Int) {
        localJournalDataDao.deleteJournalDataById(id)
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
            journalData.isSynced
        )
    }
}