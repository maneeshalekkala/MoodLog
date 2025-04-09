package uk.ac.tees.mad.moodlog.model.dataclass.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_journal_data")
data class LocalJournalData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var firestoreId: String = "",
    val userId: String = "",
    val journalContent: String = "",
    val journalDate: String = "",
    val journalTime: String = "",
    val journalMood: String = "",
    val journalLocationLatitude: Double = 0.0,
    val journalLocationLongitude: Double = 0.0,
    val journalLocationAddress: String = "",
    val journalImage: String = "",
    val isDeleted: Boolean = false,
    val needsUpdate: Boolean = false
)