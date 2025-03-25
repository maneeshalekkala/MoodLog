package uk.ac.tees.mad.moodlog.model.dataclass.journalscreen

data class JournalScreenData(
    val journalContent: String,
    val journalDate: String,
    val journalTime: String,
    val journalMood: String,
    val journalLocation: String,
    val journalImage: String,
)