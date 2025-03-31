package uk.ac.tees.mad.moodlog.model.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData

@Dao
interface LocalJournalDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(journalData: LocalJournalData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(journalData: LocalJournalData)

    @Delete
    suspend fun delete(journalData: LocalJournalData)

    @Query("SELECT * FROM local_journal_data WHERE userId = :userId")
    suspend fun getAllJournalDataForUser(userId: String): List<LocalJournalData>

    @Query("SELECT * FROM local_journal_data WHERE id = :id")
    suspend fun getJournalDataById(id: Int): LocalJournalData?

    @Query("SELECT * FROM local_journal_data WHERE isSynced = 0")
    suspend fun getAllUnsyncedJournalData(): List<LocalJournalData>

    @Query("UPDATE local_journal_data SET isSynced = 1 WHERE id = :id")
    suspend fun updateSyncStatus(id: Int)

    @Query("DELETE FROM local_journal_data WHERE userId = :userId")
    suspend fun deleteAllJournalDataForUser(userId: String)

    @Query("DELETE FROM local_journal_data WHERE id = :id")
    suspend fun deleteJournalDataById(id: Int)

    @Query(
        """
        UPDATE local_journal_data 
        SET journalContent = :journalContent, 
            journalDate = :journalDate, 
            journalTime = :journalTime, 
            journalMood = :journalMood, 
            journalLocationLatitude = :journalLocationLatitude, 
            journalLocationLongitude = :journalLocationLongitude, 
            journalLocationAddress = :journalLocationAddress, 
            journalImage = :journalImage, 
            isSynced = :isSynced
        WHERE id = :id
        """
    )
    suspend fun updateJournalData(
        id: Int,
        journalContent: String,
        journalDate: String,
        journalTime: String,
        journalMood: String,
        journalLocationLatitude: Double,
        journalLocationLongitude: Double,
        journalLocationAddress: String,
        journalImage: String,
        isSynced: Boolean
    )
}