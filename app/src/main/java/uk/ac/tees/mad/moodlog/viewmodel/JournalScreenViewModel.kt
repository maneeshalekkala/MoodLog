package uk.ac.tees.mad.moodlog.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.ac.tees.mad.moodlog.model.dataclass.location.LocationData
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository
import uk.ac.tees.mad.moodlog.model.repository.LocalJournalDataRepository
import uk.ac.tees.mad.moodlog.view.utils.LocationUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.days

class JournalScreenViewModel(
    private val authRepository: AuthRepository,
    private val localJournalDataRepository: LocalJournalDataRepository
) : ViewModel() {

    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis().toString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _sliderPosition = MutableStateFlow(2f)
    val sliderPosition: StateFlow<Float> = _sliderPosition.asStateFlow()

    private val _mood = MutableStateFlow("Neutral")
    val mood: StateFlow<String> = _mood.asStateFlow()

    private val _journalContent = MutableStateFlow("")
    val journalContent: StateFlow<String> = _journalContent.asStateFlow()

    private val _journalData = MutableStateFlow<List<LocalJournalData>>(emptyList())
    val journalData: StateFlow<List<LocalJournalData>> = _journalData.asStateFlow()

    private val _image = MutableStateFlow<String>("")
    val image: StateFlow<String> = _image.asStateFlow()

    init {
        getJournalDataForUser(authRepository.getCurrentUserId().toString())
    }

    fun updateJournalContent(newContent: String) {
        _journalContent.value = newContent
    }

    fun updateSliderPosition(newPosition: Float) {
        _sliderPosition.value = newPosition
        updateMood(
            when (sliderPosition.value) {
                0f -> "Very Dissatisfied"
                1f -> "Dissatisfied"
                2f -> "Neutral"
                3f -> "Satisfied"
                4f -> "Very Satisfied"
                else -> "Mood"
            }
        )
    }

    fun updateMood(newMood: String) {
        _mood.value = newMood
    }

    fun updateSelectedDate(newDate: String) {
        _selectedDate.value = newDate
    }

    fun updateLocation(newLocation: LocationData) {
        _location.value = newLocation
    }

    fun updateAddress(newAddress: String) {
        _address.value = newAddress
    }

    fun saveJournal() {
        viewModelScope.launch {
            //val imagePath = imageUri?.let { saveImageToInternalStorage(it,context) }
            val journalData = LocalJournalData(
                userId = authRepository.getCurrentUserId().toString(),
                journalContent = journalContent.value,
                journalDate = selectedDate.value,
                journalTime = System.currentTimeMillis().toString(),
                journalMood = mood.value,
                journalLocationLatitude = location.value?.latitude ?: 0.0,
                journalLocationLongitude = location.value?.longitude ?: 0.0,
                journalLocationAddress = address.value,
                journalImage = _image.value, // Store local path or ""
                firestoreId = ""
            )
            localJournalDataRepository.insertJournalData(journalData)
        }
    }

    private suspend fun saveImageToInternalStorage(uri: Uri, context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.filesDir, "${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                file.absolutePath
            } catch (e: Exception) {
                Log.e("JournalScreenViewModel", "Error saving image: ${e.message}")
                null
            }
        }
    }

    fun getJournalDataForUser(userId: String) {
        viewModelScope.launch {
            localJournalDataRepository.getAllJournalDataForUser(userId).collectLatest { journalList ->
                _journalData.value = journalList
            }
        }
    }

    fun deleteJournalData(journalData: LocalJournalData) {
        viewModelScope.launch {
            //soft delete
            localJournalDataRepository.updateJournalData(journalData.copy(isDeleted = true))
        }
    }

    fun updateJournalData(journalData: LocalJournalData) {
        viewModelScope.launch {
            localJournalDataRepository.updateJournalData(journalData.copy(needsUpdate = true))
        }
    }

    fun updateImage(imageUri: Uri, contentResolver: ContentResolver) {
        val base64Image = encodeImageToBase64(imageUri, contentResolver)
        _image.value = base64Image ?: ""
    }

    fun encodeImageToBase64(uri: Uri, contentResolver: ContentResolver): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                // 1. Get dimensions (without loading the whole bitmap)
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(input, null, options)

                // Reset the input stream to start again from the beginning
                inputStream.close()
                val newInputStream: InputStream? = contentResolver.openInputStream(uri)

                // 2. Calculate inSampleSize
                options.apply {
                    inJustDecodeBounds = false
                    inSampleSize = calculateInSampleSize(options, 1024, 1024) // Example: Target 1024x1024
                }

                // 3. Decode with downsampling
                var bitmap: Bitmap? = BitmapFactory.decodeStream(newInputStream, null, options)

                // 4. Read EXIF orientation
                val exifInterface = ExifInterface(contentResolver.openInputStream(uri)!!)
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                // 5. Rotate bitmap if necessary
                bitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap!!, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap!!, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap!!, 270f)
                    else -> bitmap
                }

                // 6. Compress
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)

                // 7. Encode to Base64
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(byteArray, Base64.DEFAULT)
            }
        } catch (e: Exception) {
            Log.e("Image Encoding", "Error encoding image: ${e.message}")
            null
        }
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}