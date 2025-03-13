package uk.ac.tees.mad.moodlog.model.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.moodlog.model.dataclass.firebase.AuthResult
import uk.ac.tees.mad.moodlog.model.dataclass.firebase.UserDetails

class AuthRepository(private val auth: FirebaseAuth) {
    fun signUp(email: String, pass: String): Flow<AuthResult<Boolean>> = flow {
        try {
            emit(AuthResult.Loading)
            auth.createUserWithEmailAndPassword(email, pass).await()
            emit(AuthResult.Success(true))
        } catch (e: Exception) {
            emit(AuthResult.Error(e))
        }
    }

    fun signIn(email: String, pass: String): Flow<AuthResult<Boolean>> = flow {
        try {
            emit(AuthResult.Loading)
            auth.signInWithEmailAndPassword(email, pass).await()
            emit(AuthResult.Success(true))
        } catch (e: Exception) {
            emit(AuthResult.Error(e))
        }
    }

    fun isSignedIn(): Boolean {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            // User is already authenticated
            true
        } else {
            // User is not authenticated
            false
        }
    }

    fun getCurrentUserId(): String? {
        val currentUser = auth.currentUser
        return currentUser?.uid
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUserDetails(): Flow<AuthResult<UserDetails>> = flow {
        emit(AuthResult.Loading)
        try {
            val currentUser: FirebaseUser? = auth.currentUser
            if (currentUser != null) {
                val userDetails = UserDetails(
                    userId = currentUser.uid,
                    email = currentUser.email,
                    displayName = currentUser.displayName,
                    isEmailVerified = currentUser.isEmailVerified,
                    phoneNumber = currentUser.phoneNumber,
                    photoUrl = currentUser.photoUrl
                )
                emit(AuthResult.Success(userDetails))
            } else {
                emit(AuthResult.Error(Exception("No user logged in")))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e))
        }
    }

    fun updateDisplayName(displayName: String): Flow<AuthResult<Boolean>> = flow {
        emit(AuthResult.Loading)
        try {
            val user = auth.currentUser
            if (user != null) {
                val profileUpdates =
                    UserProfileChangeRequest.Builder().setDisplayName(displayName).build()

                user.updateProfile(profileUpdates).await()
                emit(AuthResult.Success(true))
                Log.d("AuthRepository", "User display name updated.")
            } else {
                emit(AuthResult.Error(Exception("No user logged in")))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e))
            Log.e("AuthRepository", "Failed to update user display name.", e)
        }
    }

}