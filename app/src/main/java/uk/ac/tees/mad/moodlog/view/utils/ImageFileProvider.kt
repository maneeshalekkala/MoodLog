package uk.ac.tees.mad.moodlog.view.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageFileProvider : FileProvider() {
    companion object {

        fun hasCameraPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun getImageUri(context: Context): Uri {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(imageFileName, ".jpg", directory
            )
            val authority = "uk.ac.tees.mad.moodlog.fileprovider"
            return getUriForFile(
                context,
                authority,
                file
            )
        }
    }
}