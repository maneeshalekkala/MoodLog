package uk.ac.tees.mad.moodlog.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData

@Database(entities = [LocalJournalData::class], version = 1, exportSchema = false)
abstract class LocalJournalDataDatabase : RoomDatabase() {
    abstract fun localJournalDataDao(): LocalJournalDataDao
}