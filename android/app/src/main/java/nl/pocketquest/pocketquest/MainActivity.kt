package nl.pocketquest.pocketquest

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.services.android.telemetry.permissions.PermissionsManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class MainActivity : AppCompatActivity(), PermissionsListener, AnkoLogger {
    private var map: MapboxMap? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationPlugin: LocationLayerPlugin? = null
    private var locationEngine: LocationEngine? = null
    private val locationEngineWrapper = LocationEngineWrapper(this, this::onLocationChanged)
    private var currentLocation: Location? = null


    companion object {
        const val MIN_CAMERA_ZOOM = 18.0
        const val MAX_CAMERA_ZOOM = MIN_CAMERA_ZOOM
        const val DEFAULT_CAMERA_ZOOM = MIN_CAMERA_ZOOM
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info { "Starting onCreate" }
        Mapbox.getInstance(this, getString(R.string.mapbox_key))
        setContentView(R.layout.activity_main)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
            enableLocationPlugin()
            info { "Map is loaded" }
            (currentLocation ?: locationEngine?.lastLocation)?.apply(this::onLocationChanged) ?: info { "No last location found!" }
            info { "Set the last location from the map" }
        }
        lifecycle.addObserver(locationEngineWrapper)
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationPlugin() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationPlugin = LocationLayerPlugin(mapView, map!!, locationEngine).apply {
                setLocationLayerEnabled(LocationLayerMode.COMPASS)
            }
        } else {
            permissionsManager = PermissionsManager(this).also {
                it.requestLocationPermissions(this)
            }
        }
    }

    private var cameraPosition: Location
        @Deprecated("only setter", level = DeprecationLevel.HIDDEN)
        get() = throw UnsupportedOperationException()
        set(location) {
            map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(location), DEFAULT_CAMERA_ZOOM)
            )
            info { "Setting $location as new camera position " }
        }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) = Unit

    override fun onPermissionResult(granted: Boolean) {
        if (granted) enableLocationPlugin() else finish()
    }

    fun Location.toLatLng() = LatLng(this.latitude, this.longitude)

    private fun onLocationChanged(location: Location) {
        info { "new Currentlocation = $location" }
        currentLocation = location
        location.also {
            cameraPosition = location
            locationPlugin?.forceLocationUpdate(currentLocation)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        info { "Requesting updates from onStart" }
        locationEngine?.requestLocationUpdates()
        locationPlugin?.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        locationEngine?.removeLocationUpdates()
        locationPlugin?.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        locationEngine?.deactivate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView.onSaveInstanceState(outState)
    }
}
