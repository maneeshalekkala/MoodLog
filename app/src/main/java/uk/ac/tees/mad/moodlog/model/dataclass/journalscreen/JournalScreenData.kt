package uk.ac.tees.mad.moodlog.model.dataclass.journalscreen

data class JournalScreenData(
    val userId: String= "",
    val journalContent: String= "",
    val journalDate: String= "",
    val journalTime: String= "",
    val journalMood: String= "",
    val journalLocationLatitude: Double= 0.0,
    val journalLocationLongitude: Double= 0.0,
    val journalLocationAddress: String= "",
    val journalImage: String= "",
    val isSynced: Boolean = false
)