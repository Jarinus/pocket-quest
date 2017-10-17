package nl.pocketquest.pocketquest

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Tasks
import com.mapbox.services.android.telemetry.location.LocationEngine
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


/**
 * Sample LocationEngine using Google Play Services
 */
class GoogleLocationEngine(var context: Context) : LocationEngine(), AnkoLogger {
    private val locationCallBack = Callback(this::onLocationChanged)
    private val locationClient = LocationServices.getFusedLocationProviderClient(this.context)

    override fun activate() {
        locationListeners.forEach { it.onConnected() }
    }

    override fun isConnected(): Boolean {
        return true
    }

    override fun deactivate() {
        info { "Deactivated" }
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation(): Location? = Tasks.await(locationClient.lastLocation)


    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates() {
        info { "Requesting location updates" }
        val mLocationRequest = createLocationRequest()
        validateLocationSettings(mLocationRequest)
        locationClient.requestLocationUpdates(mLocationRequest, locationCallBack, null)
    }

    private fun validateLocationSettings(mLocationRequest: LocationRequest) {
        val locationSettingsRequest = LocationSettingsRequest.Builder()
                .also { it.addLocationRequest(mLocationRequest) }
                .build()
        LocationServices.getSettingsClient(this.context).checkLocationSettings(locationSettingsRequest)
    }

    private fun createLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest()
                .also { it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY }
                .also { it.interval = this.interval.toLong() }
                .also { it.fastestInterval = this.fastestInterval.toLong() }
        return mLocationRequest
    }

    override fun removeLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallBack)
    }

    fun onLocationChanged(location: Location) =
            locationListeners.forEach { it.onLocationChanged(location) }

    private class Callback(var callBack: (Location) -> Unit) : LocationCallback() {

        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            result?.lastLocation?.also(callBack)
        }

    }

    companion object {
        @Synchronized
        fun getLocationEngine(context: Context) = GoogleLocationEngine(context)

    }
}
