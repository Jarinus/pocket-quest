package nl.pocketquest.pocketquest

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMapOptions
import com.mapbox.mapboxsdk.maps.SupportMapFragment
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.services.android.telemetry.permissions.PermissionsManager
import nl.pocketquest.pocketquest.game.Game
import nl.pocketquest.pocketquest.game.GameObject
import nl.pocketquest.pocketquest.location.LocationEngineWrapper
import nl.pocketquest.pocketquest.sprites.GameObjectAnimator
import nl.pocketquest.pocketquest.sprites.SpriteSheetCreator
import nl.pocketquest.pocketquest.utils.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class MainActivity : AppCompatActivity(), PermissionsListener, AnkoLogger {
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
        Mapbox.getInstance(this, getString(R.string.mapbox_key))

        enableLocationPlugin()
        lifecycle.addObserver(locationEngineWrapper)

        val mapFragment : SupportMapFragment
        if (savedInstanceState == null){
            val transaction = supportFragmentManager.beginTransaction()
            val options = buildMapboxOptions {
                styleUrl = getString(R.string.mapbox_custom_style)
                zoomPreference = 18.1
                enabledgestures{
                    all = false
                }
                cameraPosition {
                    locationEngineWrapper.location?.also { target(it.toLatLng()) }
                    zoom(18.1)
                    tilt(50.25)
                }
            }
            mapFragment = SupportMapFragment.newInstance(options)
            transaction.replace(R.id.mapFragment, mapFragment, "com.mapbox.map").commit()
        } else {
            mapFragment = supportFragmentManager.findFragmentByTag("com.mapbox.map") as SupportMapFragment
        }

        mapFragment.getMapAsync {
            map = it
            info { "About to add the player marker" }
            addPlayerMarker()
            info { "Added the player marker" }

            locationEngineWrapper.location?.also { map?.setCameraPosition(it) }
            info { "Map is loaded" }
            locationEngineWrapper.location?.apply(this::onLocationChanged) ?: info { "No last location found!" }
            info { "Set the last location from the map" }
        }
    }

    private fun GameObject.animate(frames: Sequence<Bitmap>, duration: Int)
            = GameObjectAnimator(this@MainActivity, this, frames, duration).apply { start() }

    private fun addPlayerMarker() {
        val frames = SpriteSheetCreator(decodeRss(R.drawable.santasprite), 4 xy 4).frames
        player = GameObject(0 latLong 0, loadImage(R.drawable.knight)).also {
            it.animate(frames, 42)
            Game(map!!) += it
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationPlugin() {
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
        if (granted) enableLocationPlugin() else finish()
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
