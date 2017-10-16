package nl.pocketquest.pocketquest

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleOwner
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
import com.mapbox.services.android.location.LostLocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import com.mapbox.services.android.telemetry.location.LocationEnginePriority
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.services.android.telemetry.permissions.PermissionsManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity(), PermissionsListener, AnkoLogger {

    private var map: MapboxMap? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationPlugin: LocationLayerPlugin? = null
    private var locationEngine: LocationEngine? = null
    private val locationEngineWrapper = LocationEngineWrapper(this, this::onLocationChanged)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info { "Starting oncreate" }
        lifecycle.addObserver(locationEngineWrapper)
        Mapbox.getInstance(this, getString(R.string.mapbox_key))
        setContentView(R.layout.activity_main)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
            it.cameraZoom = 18.0..22.0

            enableLocationPlugin()
        }
    }

    var MapboxMap.cameraZoom: ClosedFloatingPointRange<Double>
        get() = minZoomLevel..maxZoomLevel
        set(value) {
            setMaxZoomPreference(value.endInclusive)
            setMinZoomPreference(value.start)
        }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        info { "Enabling location plugin" }
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine()
            info { "Getting the last location" }
            onLocationChanged(locationEngine!!.lastLocation)
            info { "Got the last location" }
            locationPlugin = LocationLayerPlugin(mapView, map!!, locationEngine).apply {
                setLocationLayerEnabled(LocationLayerMode.COMPASS)
            }
        } else {
            permissionsManager = PermissionsManager(this).also {
                it.requestLocationPermissions(this)
            }
        }
    }


    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine() {
        locationEngine = locationEngineWrapper.locationEngine
    }


    var cameraPosition: Location
        @Deprecated("only setter", level = DeprecationLevel.HIDDEN) get() = throw UnsupportedOperationException()
        set(location) = map!!.animateCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(location), 13.0)
        )

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) = Unit
    override fun onPermissionResult(granted: Boolean) {
        if (granted) enableLocationPlugin() else finish()
    }

    fun onLocationChanged(location: Location?) {
        location?.also {
            toast("Lat: ${it.latitude}. Long: ${it.longitude}")
            cameraPosition = location
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        info { "requesting updates from onStart" }
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