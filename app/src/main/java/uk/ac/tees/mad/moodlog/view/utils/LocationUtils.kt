package uk.ac.tees.mad.moodlog.view.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import uk.ac.tees.mad.moodlog.model.dataclass.location.LocationData
import uk.ac.tees.mad.moodlog.viewmodel.JournalScreenViewModel
import java.util.Locale

class LocationUtils(val context: Context) {

    private val _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(viewmodel: JournalScreenViewModel) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(loactionResult: LocationResult) {
                super.onLocationResult(loactionResult)
                loactionResult.lastLocation?.let {
                    val location = LocationData(
                        latitude = it.latitude, longitude = it.longitude
                    )
                    viewmodel.updateLocation(location)
                }
            }
        }
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60000).build()
        _fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun reverseGeocodeLocation(location: LocationData): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val coordinate = LatLng(location.latitude, location.longitude)
        val addresses: MutableList<Address>? =
            geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)
        return if (addresses != null && addresses.isNotEmpty()) {
            addresses[0].getAddressLine(0)
        } else {
            "Address not found"
        }
    }
}