package uk.ac.tees.mad.moodlog.model.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData

@Dao
interface LocalJournalDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(journalData: LocalJournalData): Long

    @Upsert
    suspend fun upsert(journalData: LocalJournalData)

    @Delete
    suspend fun delete(journalData: LocalJournalData)

    @Query("SELECT * FROM local_journal_data WHERE userId = :userId AND isDeleted = 0")
    fun getAllJournalDataForUser(userId: String): Flow<List<LocalJournalData>> // Return Flow

    @Query("SELECT * FROM local_journal_data WHERE id = :id")
    suspend fun getJournalDataById(id: Int): LocalJournalData?

    @Query("SELECT * FROM local_journal_data WHERE (firestoreId ='' OR needsUpdate = 1 OR isDeleted= 1)")
    fun getJournalsForSync(): Flow<List<LocalJournalData>>

    @Query("UPDATE local_journal_data SET firestoreId = :firestoreId WHERE id = :id")
    suspend fun updateFirestoreId(id: Int, firestoreId: String)

    @Query("DELETE FROM local_journal_data WHERE userId = :userId")
    suspend fun deleteAllJournalDataForUser(userId: String): Int

    @Query("DELETE FROM local_journal_data WHERE id = :id")
    suspend fun deleteJournalDataById(id: Int): Int

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
            needsUpdate = :needsUpdate,
            isDeleted = :isDeleted
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
        needsUpdate: Boolean,
        isDeleted: Boolean
    ): Int
}