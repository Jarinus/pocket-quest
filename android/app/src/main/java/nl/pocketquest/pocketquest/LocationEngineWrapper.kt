package nl.pocketquest.pocketquest

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapbox.services.android.telemetry.location.AndroidLocationEngine

import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import com.mapbox.services.android.telemetry.location.LocationEnginePriority
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class LocationEngineWrapper(private val context: Context, var locationListener: (Location) -> Unit) : LocationEngineListener, AnkoLogger, LifecycleObserver {
    companion object {
        const val DEFAULT_FASTEST_INTERVAL = 500
        const val DEFAULT_INTERVAL = 1000
        const val ACCURACY_MODE = LocationEnginePriority.HIGH_ACCURACY
        val LOCATION_PROVIDER = AndroidLocationEngine::getLocationEngine
    }

    lateinit var locationEngine: LocationEngine


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(savedInstanceState: Bundle?) {
        info { "Oncreate of LocationEngineWrapper" }
        createLocationEngine()
    }

    private fun createLocationEngine() {
        locationEngine = LOCATION_PROVIDER(context).also {
            it.fastestInterval = DEFAULT_FASTEST_INTERVAL
            it.interval = DEFAULT_INTERVAL
            it.priority = ACCURACY_MODE
            it.addLocationEngineListener(this)
        }
    }

    @SuppressLint("MissingPermission")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        locationEngine.activate()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        locationEngine.removeLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    override fun onConnected() {
        info { "Connected to engine, we can now request updates." }
        locationEngine.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        location?.also {
            info { "New location received: $location" }
            locationListener(it)
        }
    }
}
