package uk.ac.tees.mad.moodlog.model.dataclass.firebase

sealed class FirestoreResult<out T> {
    object Loading : FirestoreResult<Nothing>()
    data class Success<out T>(val data: T) : FirestoreResult<T>()
    data class Error(val exception: Exception) : FirestoreResult<Nothing>()
}