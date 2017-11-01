package nl.pocketquest.pocketquest

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.SupportMapFragment
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.services.android.telemetry.permissions.PermissionsManager
import nl.pocketquest.pocketquest.game.Game
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.location.LocationEngineWrapper
import nl.pocketquest.pocketquest.sprites.SpriteSheetCreator
import nl.pocketquest.pocketquest.utils.*
import org.jetbrains.anko.info

private const val MAPBOX_TAG = "com.mapbox.map"
private const val ANIMATION_DURATION = 42
class MainActivity : BaseActivity(), PermissionsListener {
    private var map: MapboxMap? = null
    private var permissionsManager: PermissionsManager? = null
    private val locationEngineWrapper = LocationEngineWrapper(this, this::onLocationChanged)
    private var currentLocation: Location? = null
    private var player: GameObject? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info { "Starting onCreate" }
        setContentView(R.layout.activity_main)
        requestLocationPermission()
        Mapbox.getInstance(this, getString(R.string.mapbox_key))
        lifecycle.addObserver(locationEngineWrapper)
        createOrLoadMapView(savedInstanceState).getMapAsync(this::initializeMap)
    }

    private fun createOrLoadMapView(savedInstanceState: Bundle?): SupportMapFragment {
        savedInstanceState ?: (supportFragmentManager.findFragmentByTag(MAPBOX_TAG) as? SupportMapFragment)?.also { return it }
        val mapFragment = SupportMapFragment.newInstance(createMapboxOptions())
        supportFragmentManager.doTransaction {
            replace(R.id.mapFragment, mapFragment, MAPBOX_TAG)
        }
        return mapFragment
    }

    private fun createMapboxOptions() = buildMapboxOptions {
        styleUrl = getString(R.string.mapbox_custom_style)
        zoomPreference = SETTINGS.MAPBOX_MAP.CAMERA_ZOOM
        enabledgestures {
            all = false
        }
        cameraPosition {
            locationEngineWrapper.location?.also { target(it.toLatLng()) }
            zoom(SETTINGS.MAPBOX_MAP.CAMERA_ZOOM)
            tilt(SETTINGS.MAPBOX_MAP.CAMERA_TILT)
        }
    }


    private fun initializeMap(mapboxMap: MapboxMap) {
            map = mapboxMap
            info { "About to add the player marker" }
            addPlayerMarker()
            info { "Added the player marker" }
            locationEngineWrapper.location?.also { map?.setCameraPosition(it) }
            info { "Map is loaded" }
            locationEngineWrapper.location?.apply(this@MainActivity::onLocationChanged) ?: info { "No last location found!" }
            info { "Set the last location from the map" }
    }

    private fun addPlayerMarker() {
        val frames = SpriteSheetCreator(decodeResource(R.drawable.santasprite), 4 xy 4).frames
        player = GameObject(0 latLong 0, loadImage(R.drawable.knight)).also {
            it.animate(frames, ANIMATION_DURATION)
            Game(map!!) += it
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun requestLocationPermission() {
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
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

    private fun onLocationChanged(location: Location) {
        info { "new Currentlocation = $location" }
        runOnUiThread {
            map?.setCameraPosition(location)
            player?.location = location.toLatLng()
        }
        currentLocation = location
    }
}
