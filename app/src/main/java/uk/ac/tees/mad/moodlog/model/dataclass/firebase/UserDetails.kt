package uk.ac.tees.mad.moodlog.model.dataclass.firebase

import android.net.Uri

data class UserData(
    val userDetails: UserDetails? = null, val userId: String? = null
)

data class UserDetails(
    val userId: String,
    val email: String?,
    val displayName: String? = "Not Available",
    val isEmailVerified: Boolean,
    val phoneNumber: String? = "Not Available",
    val photoUrl: Uri? = null
)