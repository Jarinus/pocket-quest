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
                    val best = currentBest
                    when {
                        isBetterLocation(it, best) -> locationListener(it)
                        best != null -> locationListener(best)
                    }
                }
    }

    private var currentBest: Location? = null

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
        if (isBetterLocation(newLocation, currentBest)) {
            currentBest = newLocation
            info { "New location received: $newLocation." }
            locationListener(newLocation)
        }
    }

    private val THIRTY_SECONDS = 1000 * 30

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true
        }


        // Check whether the new location fix is newer or older
        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > THIRTY_SECONDS
        val isSignificantlyOlder = timeDelta < -THIRTY_SECONDS
        val isNewer = timeDelta > 0

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(location.provider,
                currentBestLocation.provider)

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }

    /** Checks whether two providers are the same  */
    private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }
}
