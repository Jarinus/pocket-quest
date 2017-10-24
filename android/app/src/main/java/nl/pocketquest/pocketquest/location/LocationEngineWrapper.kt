package nl.pocketquest.pocketquest.location

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.location.Location
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import nl.pocketquest.pocketquest.SETTINGS.LOCATION_ENGINE.ACCURACY_MODE
import nl.pocketquest.pocketquest.SETTINGS.LOCATION_ENGINE.DEFAULT_FASTEST_INTERVAL
import nl.pocketquest.pocketquest.SETTINGS.LOCATION_ENGINE.DEFAULT_INTERVAL
import nl.pocketquest.pocketquest.SETTINGS.LOCATION_ENGINE.LOCATION_PROVIDER
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

@SuppressLint("MissingPermission")
class LocationEngineWrapper(
        private val context: Context,
        private var locationListener: (Location) -> Unit
) : LocationEngineListener, AnkoLogger, LifecycleObserver {

    private var currentLocation : Location? = null
    val location : Location? get() = currentLocation

    lateinit var locationEngine: LocationEngine
        private set

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(owner: LifecycleOwner) {
        info { "OnCreate was called." }
        locationEngine = LOCATION_PROVIDER(context).also {
            it.fastestInterval = DEFAULT_FASTEST_INTERVAL
            it.interval = DEFAULT_INTERVAL
            it.priority = ACCURACY_MODE
            it.addLocationEngineListener(this)
            currentLocation = it.lastLocation
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onstart(owner: LifecycleOwner){
        info { "Requesting updates from onStart" }
        locationEngine.requestLocationUpdates()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(owner: LifecycleOwner) {
        locationEngine.activate()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(owner: LifecycleOwner) {
        locationEngine.removeLocationUpdates()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(owner: LifecycleOwner){
        locationEngine.removeLocationUpdates()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner){
        locationEngine.deactivate()
    }


    override fun onConnected() {
        info { "Connected to engine." }
        locationEngine.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        location?.also {
            info { "New location received: $location." }
            currentLocation = it
            locationListener(it)
        }
    }
}
