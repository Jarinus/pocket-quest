package nl.pocketquest.pocketquest.location

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import nl.pocketquest.pocketquest.SETTINGS
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

@SuppressLint("MissingPermission")
class LocationEngineWrapper(
        private val context: Context,
        private var locationListener: (Location) -> Unit
) : AnkoLogger, LocationCallback(), LifecycleObserver {

    fun loadLastLocation() {
        LocationServices.getFusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener {
                    onNewLocation(it)
                }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(owner: LifecycleOwner) {
        val locationRequest = LocationRequest().also {
            it.priority = SETTINGS.LOCATION_ENGINE.ACCURACY_MODE
            it.interval = SETTINGS.LOCATION_ENGINE.DEFAULT_INTERVAL
            it.fastestInterval = SETTINGS.LOCATION_ENGINE.DEFAULT_FASTEST_INTERVAL
        }
        val locationSettingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build()
        LocationServices.getSettingsClient(context)
                .checkLocationSettings(locationSettingsRequest)
        LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest, this, Looper.myLooper())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(owner: LifecycleOwner) {
        info { "Requesting updates from onStart" }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(owner: LifecycleOwner) = Unit

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(owner: LifecycleOwner) = Unit

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) = Unit

    override fun onLocationResult(locationResult: LocationResult) {
        locationResult.lastLocation?.also {
            onNewLocation(it)
        }
    }

    private fun onNewLocation(newLocation: Location) {
        info { "New location received: $newLocation." }
        locationListener(newLocation)
    }
}
