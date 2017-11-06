package nl.pocketquest.pocketquest.location

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.location.Location
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.services.android.telemetry.permissions.PermissionsManager
import nl.pocketquest.pocketquest.SETTINGS
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

@SuppressLint("MissingPermission")
class LocationEngineWrapper(
        private val context: Context,
        private var locationListener: (Location) -> Unit
) :
        Activity(), LocationEngineListener, PermissionsListener, AnkoLogger, LifecycleObserver {


    private lateinit var locationEngine: LocationEngine
    private var permissionsManager: PermissionsManager? = null

    fun getLastLocation() = locationEngine.lastLocation

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(owner: LifecycleOwner) {
        requestLocationPermission()
        locationEngine = SETTINGS.LOCATION_ENGINE.LOCATION_PROVIDER(context).also {
            it.fastestInterval = SETTINGS.LOCATION_ENGINE.DEFAULT_FASTEST_INTERVAL
            it.interval = SETTINGS.LOCATION_ENGINE.DEFAULT_INTERVAL
            it.priority = SETTINGS.LOCATION_ENGINE.ACCURACY_MODE
            it.addLocationEngineListener(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(owner: LifecycleOwner) {
        info { "Requesting updates from onStart" }
        locationEngine.requestLocationUpdates()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(owner: LifecycleOwner) = locationEngine.activate()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(owner: LifecycleOwner) = locationEngine.removeLocationUpdates()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) = locationEngine.deactivate()

    override fun onConnected() {
        info { "Connected to engine." }
        locationEngine.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        location?.also {
            info { "New location received: $location." }
            locationListener(it)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun requestLocationPermission() {
        if (!PermissionsManager.areLocationPermissionsGranted(context)) {
            permissionsManager = PermissionsManager(this).also {
                it.requestLocationPermissions(this)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) = Unit

    override fun onPermissionResult(granted: Boolean) {
        if (!granted) finish()
    }
}
